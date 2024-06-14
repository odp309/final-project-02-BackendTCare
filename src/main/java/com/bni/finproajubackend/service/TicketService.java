package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.repository.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;

@Service
public class TicketService implements TicketInterface {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private static final Marker TICKETS_MARKER = MarkerFactory.getMarker("TICKETS");

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TicketsHistoryRepository ticketHistoryRepository;
    @Autowired
    private TicketResponseTimeRepository ticketResponseTimeRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LoggerService loggerService;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException {
        Tickets ticket = ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        TicketStatus oldStatus = ticket.getTicketStatus();
        if (oldStatus != TicketStatus.Selesai) {
            TicketStatus nextStatus = oldStatus == TicketStatus.Diajukan ? TicketStatus.DalamProses
                    : TicketStatus.Selesai;

            ticket.setTicketStatus(nextStatus);
            ticketsRepository.save(ticket);

            // Get admin details from authentication
            String username = authentication.getName();
            Admin admin = adminRepository.findByUsername(username);

            if (admin == null)
                throw new RuntimeException("User not found");

            Long statusLevel = getStatusLevel(ticket.getTicketStatus());

            // Create a new ticket history entry
            TicketHistory ticketHistory = new TicketHistory();
            ticketHistory.setTicket(ticket);
            ticketHistory.setAdmin(admin);
            //ticketHistory.setDescription("Ticket status updated from " + oldStatus + " to " + nextStatus);
            ticketHistory.setDescription(nextStatus == TicketStatus.DalamProses ? "Laporan dalam proses" :
                    nextStatus == TicketStatus.Selesai ? "Laporan selesai diproses" : "");
            ticketHistory.setDate(new Date());
            ticketHistory.setLevel(statusLevel);
            ticketHistory.setCreatedAt(LocalDateTime.now());
            ticketHistory.setUpdatedAt(LocalDateTime.now());
            ticketHistoryRepository.save(ticketHistory);

            // Handle TicketResponseTime when status is Ditutup
            if (nextStatus == TicketStatus.Selesai) {
                handleTicketResponseTime(ticket);
            }

            // Send email notification
            emailService.sendNotification(ticket);
        }

        return ticket;
    }

    private Long getStatusLevel(TicketStatus status) {
        return status == TicketStatus.Diajukan ? 1L :
                status == TicketStatus.DalamProses ? 2L : 3L;
    }

    private void handleTicketResponseTime(Tickets ticket) {
        TicketResponseTime responseTime = new TicketResponseTime();
        responseTime.setTicket(ticket);

        LocalDate createdAt = ticket.getCreatedAt().toLocalDate();
        LocalDate closedAt = LocalDate.now();
        long daysBetween = Duration.between(createdAt.atStartOfDay(), closedAt.atStartOfDay()).toDays();

        responseTime.setResponseTime(daysBetween);
        responseTime.setCreatedAt(LocalDateTime.now());
        responseTime.setUpdatedAt(LocalDateTime.now());

        ticketResponseTimeRepository.save(responseTime);
    }

    @Override
    public CustomerTicketDetailsReportDTO getCustomerTicketDetails(String ticketNumber) {
        Tickets ticket = ticketsRepository.findByTicketNumber(ticketNumber);
        if (ticket == null) {
            throw new EntityNotFoundException("Ticket not found");
        }

        return CustomerTicketDetailsReportDTO.builder()
                .reporter_detail(
                        CustomerTicketDetailsReportDTO.ReporterDetail.builder()
                                .nama(ticket.getTransaction().getAccount().getNasabah().getFirst_name())
                                .account_number(ticket.getTransaction().getAccount().getAccountNumber())
                                .build()
                )
                .report_detail(
                        CustomerTicketDetailsReportDTO.ReportDetail.builder()
                                .transaction_date(ticket.getTransaction().getCreatedAt())
                                .transaction_number(ticket.getTransaction().getId().toString())
                                .amount(ticket.getTransaction().getAmount())
                                .category(switch (ticket.getTicketCategory()) {
                                    case Payment -> "Gagal Payment";
                                    case TopUp -> "Gagal Top Up";
                                    case Transfer -> "Transfer";
                                })
                                .description(ticket.getDescription())
                                .build()
                )
                .report_status_detail(
                        CustomerTicketDetailsReportDTO.ReportStatusDetail.builder()
                                .report_date(ticket.getCreatedAt())
                                .ticket_number(ticket.getTicketNumber())
                                .status(switch (ticket.getTicketStatus()) {
                                    case Diajukan -> "Diajukan";
                                    case DalamProses -> "Dalam proses";
                                    case Selesai -> "Selesai";
                                })
                                .reference_num(ticket.getReferenceNumber())
                                .build()
                )
                .build();
    }

    @Override
    public TicketDetailsReportDTO getTicketDetails(String ticketNumber) {
        Tickets ticket = ticketsRepository.findByTicketNumber(ticketNumber);
        if (ticket == null) {
            throw new EntityNotFoundException("Ticket not found");
        }

        return TicketDetailsReportDTO.builder()
                .reporter_detail(
                        TicketDetailsReportDTO.ReporterDetail.builder()
                                .nama(ticket.getTransaction().getAccount().getNasabah().getFirst_name())
                                .account_number(ticket.getTransaction().getAccount().getAccountNumber())
                                .address(ticket.getTransaction().getAccount().getNasabah().getAddress())
                                .no_handphone(ticket.getTransaction().getAccount().getNasabah().getNoHP())
                                .build()
                )
                .report_detail(
                        TicketDetailsReportDTO.ReportDetail.builder()
                                .transaction_date(ticket.getTransaction().getCreatedAt())
                                .amount(ticket.getTransaction().getAmount())
                                .category(switch (ticket.getTicketCategory()) {
                                    case Payment -> "Gagal Payment";
                                    case TopUp -> "Gagal Top Up";
                                    case Transfer -> "Gagal Transfer";
                                })
                                .description(ticket.getDescription())
                                .reference_num(ticket.getReferenceNumber())
                                .build()
                )
                .report_status_detail(
                        TicketDetailsReportDTO.ReportStatusDetail.builder()
                                .report_date(ticket.getCreatedAt())
                                .ticket_number(ticket.getTicketNumber())
                                .status(switch (ticket.getTicketStatus()) {
                                    case Diajukan -> "Diajukan";
                                    case DalamProses -> "Dalam Proses";
                                    case Selesai -> "Selesai";
                                })
                                .build()
                )
                .build();
    }

    @Override
    public List<TicketHistoryResponseDTO> getTicketHistory(long id) {
        List<TicketHistory> ticketHistoryList = ticketHistoryRepository.findAll();
        List<TicketHistoryResponseDTO> responseDTOList = new ArrayList<>();

        for (TicketHistory ticketHistory : ticketHistoryList) {
            TicketHistoryResponseDTO responseDTO = new TicketHistoryResponseDTO();
            responseDTO.setPic(getAdminFullName(ticketHistory.getAdmin()));
            responseDTO.setDescription(ticketHistory.getDescription());
            responseDTO.setDate(ticketHistory.getDate());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    @Override
    public String createTicketNumber(Transaction transaction) {
        String categoryCode = switch (transaction.getCategory()) {
            case Transfer -> "TF";
            case TopUp -> "TU";
            case Payment -> "PY";
            default -> "";
        };

        LocalDateTime createdAt = transaction.getCreatedAt();
        String year = String.valueOf(createdAt.getYear());
        String month = String.format("%02d", createdAt.getMonthValue());
        String day = String.format("%02d", createdAt.getDayOfMonth());

        String transactionId = String.format("%010d", transaction.getId());

        return categoryCode + year + month + day + transactionId;
    }

    @Override
    public TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO) {
        Transaction transaction = transactionRepository.findById(ticketRequestDTO.getTransaction_id())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        TicketCategories category = switch (transaction.getCategory()) {
            case Payment -> TicketCategories.Payment;
            case TopUp -> TicketCategories.TopUp;
            default -> TicketCategories.Transfer;
        };

        DivisionTarget divisionTarget = switch (transaction.getCategory()) {
            case Payment -> DivisionTarget.DGO;
            case TopUp -> DivisionTarget.WPP;
            default -> DivisionTarget.CXC;
        };

        Tickets ticket = Tickets.builder()
                .ticketNumber(createTicketNumber(transaction))
                .transaction(transaction)
                .ticketCategory(category)
                .description("Complaint Ticket")
                .divisionTarget(divisionTarget)
                .ticketStatus(TicketStatus.Diajukan)
                .referenceNumber(ticketRequestDTO.isReopen_ticket() ? ticketRequestDTO.getReference_number() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);

        createTicketHistory(savedTicket);

        checkProblem(transaction);

        return TicketResponseDTO.builder()
                .transaction_id(transaction.getId())
                .account_number(transaction.getAccount().getAccountNumber())
                .ticket_category(savedTicket.getTicketCategory().toString())
                .reopen_ticket(savedTicket.getReferenceNumber() != null)
                .reference_number(savedTicket.getReferenceNumber() != null ? savedTicket.getReferenceNumber() : null)
                .build();
    }

    @Async
    private void checkProblem(Transaction transaction) {
        Transaction transactionSender = transaction;
        Account accountRecipient = transaction.getRecipient_account();

        Specification<Transaction> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(transactionSender.getId()).ifPresent(id ->
                    predicates.add(builder.equal(root.get("referenced_id"), id))
            );

            Optional.of(transactionSender.getTransaction_type() != TransactionType.In ? TransactionType.Out : TransactionType.In).ifPresent(type ->
                    predicates.add(builder.equal(root.get("transaction_type"), type))
            );

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        List<Transaction> transactionRecipient = transactionRepository.findAll(spec);
        int recipientSize = transactionRecipient.size();

        if (recipientSize == 0) {
            processTicket(transaction, "IP {}, Transaction not found, Continuing ticket to division");
        } else if (recipientSize > 1) {
            processTicket(transaction, "IP {}, Something wrong with this Transaction, Continuing ticket to division");
        } else {
            processTicket(transaction, "IP {}, Transaction found, Closing Tickets by System");
            createSingleTicketHistory(transaction.getTickets(), adminRepository.findByUsername("system"), "Laporan Selesai", 4L);
            createSingleTicketHistory(transaction.getTickets(), adminRepository.findByUsername("system"), "Laporan Diterima Pelapora", 5L);
        }
    }

    private void processTicket(Transaction transaction, String logMessage) {
        createSingleTicketHistory(transaction.getTickets(), adminRepository.findByUsername("system"), "Laporan Diproses", 3L);
        logger.info(TICKETS_MARKER, logMessage, loggerService.getClientIp());
    }


    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin12");

        createSingleTicketHistory(ticket, admin, "Laporan Dibuka", 1L);
        createSingleTicketHistory(ticket, admin, "Laporan " + ticket.getTicketStatus(), 2L);
    }

    private void createSingleTicketHistory(Tickets ticket, Admin admin, String description, Long level) {
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription(description);
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(level);
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());

        ticketHistoryRepository.save(ticketHistory);
    }

    public String getAdminFullName(@NotNull Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }

    @Override
    public ComplaintResponseDTO getFormComplaint(long id) throws Exception {
        logger.debug(TICKETS_MARKER, "IP {}, Form Complaint Requested", loggerService.getClientIp());
        try {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
            logger.info(TICKETS_MARKER, "IP {}, Transaction Detail acquired for Form Complaint", loggerService.getClientIp());
            return ComplaintResponseDTO.builder()
                    .transaction_id(transaction.getId())
                    .account_number(transaction.getAccount().getAccountNumber())
                    .ticket_category(transaction.getCategory().toString())
                    .reopen_ticket(transaction.getTickets() != null)
                    .reference_number(transaction.getTickets() != null ? transaction.getTickets().getTicketNumber() : null)
                    .build();
        } catch (Exception e) {
            logger.error(TICKETS_MARKER, "IP {}, Error getting form complaint", loggerService.getClientIp(), e);
            throw new Exception("Error getting customer ticket details");
        }
    }


}
