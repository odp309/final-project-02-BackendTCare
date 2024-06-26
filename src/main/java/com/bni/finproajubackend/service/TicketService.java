package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.repository.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class TicketService implements TicketInterface {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private static final Marker TICKETS_MARKER = MarkerFactory.getMarker("TICKETS");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
    private TicketFeedbackRepository ticketFeedbackRepository;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SmartTicketService smartTicketService;

    @Transactional
    public Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException {
        Tickets ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        TicketStatus oldStatus = ticket.getTicketStatus();
        if (oldStatus != TicketStatus.Selesai) {
            TicketStatus nextStatus = oldStatus == TicketStatus.Diajukan ? TicketStatus.DalamProses
                    : TicketStatus.Selesai;

            ticket.setTicketStatus(nextStatus);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketsRepository.save(ticket);

            if (nextStatus == TicketStatus.Selesai) {
                ticket.setIsClosed(true);
            }

            // Get admin details from authentication
            String username = authentication.getName();
            Admin admin = adminRepository.findByUsername(username);

            if (admin == null)
                throw new RuntimeException("User not found");

            Long statusLevel = getStatusLevel(nextStatus);

            // Create a new ticket history entry
            saveTicketHistory(ticket, admin, nextStatus, statusLevel);

            // Send email notification
            emailService.sendNotification(ticket);

            // Handle TicketResponseTime when status is Ditutup
            if (nextStatus == TicketStatus.Selesai) {
                handleTicketResponseTime(ticket);
            }
        }
        return ticket;
    }

    private void saveTicketHistory(Tickets ticket, Admin admin, TicketStatus status, Long statusLevel) {
        String description = status == TicketStatus.DalamProses ? "laporan dalam proses" :
                status == TicketStatus.Selesai ? "laporan selesai diproses" : "";

        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription(description);
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(statusLevel);
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());
        ticketHistoryRepository.save(ticketHistory);

        if (status == TicketStatus.Selesai) {
            TicketHistory additionalHistory = new TicketHistory();
            additionalHistory.setTicket(ticket);
            additionalHistory.setAdmin(admin);
            additionalHistory.setDescription("laporan diterima pelapor");
            additionalHistory.setDate(new Date());
            additionalHistory.setLevel(statusLevel + 1L);
            additionalHistory.setCreatedAt(LocalDateTime.now());
            additionalHistory.setUpdatedAt(LocalDateTime.now());
            ticketHistoryRepository.save(additionalHistory);
        }
    }


    private Long getStatusLevel(TicketStatus status) {
        return status == TicketStatus.Diajukan ? 2L :
                status == TicketStatus.DalamProses ? 3L :
                        status == TicketStatus.Selesai ? 4L :
                                5L;
    }

    private void handleTicketResponseTime(Tickets ticket) {
        TicketResponseTime responseTime = new TicketResponseTime();
        responseTime.setTicket(ticket);

        LocalDate createdAt = ticket.getCreatedAt().toLocalDate();
        LocalDate closedAt = LocalDate.now();
        long daysBetween = Duration.between(createdAt.atStartOfDay(), closedAt.atStartOfDay()).toDays();

        responseTime.setResponseTime((int) daysBetween);
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
        Tickets ticket_reference = ticketsRepository.findByReferenceNumber(ticket.getTicketNumber());

        TicketHistory latestHistory = ticketHistoryRepository
                .findFirstByTicketOrderByCreatedAtDesc(ticket);

        LocalDateTime reportDate = latestHistory != null ? latestHistory.getCreatedAt() : ticket.getCreatedAt();

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
                                .report_date(reportDate)
                                .ticket_number(ticket.getTicketNumber())
                                .status(switch (ticket.getTicketStatus()) {
                                    case Diajukan -> "Diajukan";
                                    case DalamProses -> "Dalam proses";
                                    case Selesai -> "Selesai";
                                })
                                .reference_num(ticket.getReferenceNumber())
                                .isReopened(ticket_reference != null)
                                .next_reference_num(ticket_reference != null ? ticket_reference.getTicketNumber() : null)
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
        Tickets ticket_reference = ticketsRepository.findByReferenceNumber(ticket.getTicketNumber());

        TicketHistory latestHistory = ticketHistoryRepository
                .findFirstByTicketOrderByCreatedAtDesc(ticket);

        LocalDateTime reportDate = latestHistory != null ? latestHistory.getCreatedAt() : ticket.getCreatedAt();

        String pic = getAdminFullName(ticket.getAdmin()) + " - " + ticket.getAdmin().getNpp();

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
                                .transaction_number(ticket.getTransaction().getId())
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
                                .report_date(reportDate)
                                .ticket_number(ticket.getTicketNumber())
                                .status(switch (ticket.getTicketStatus()) {
                                    case Diajukan -> "Diajukan";
                                    case DalamProses -> "Dalam Proses";
                                    case Selesai -> "Selesai";
                                })
                                .isReopened(ticket_reference != null)
                                .next_reference_num(ticket_reference != null ? ticket_reference.getTicketNumber() : null)
                                .pic(pic)
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
        if (transaction.getTickets().size() == 0) {
            createdAt = LocalDateTime.now();
        }

        // Ambil 2 digit terakhir dari tahun
        String year = String.format("%02d", createdAt.getYear() % 100);
        // Ambil 2 digit pertama dari detik
        String seconds = String.format("%02d", createdAt.getSecond()).substring(0, 2);

        String month = String.format("%02d", createdAt.getMonthValue());
        String day = String.format("%02d", createdAt.getDayOfMonth());

        // Tentukan panjang dari bagian awal ticket number
        String partialTicketNumber = categoryCode + year + month + day + seconds;
        int partialLength = partialTicketNumber.length();

        // Panjang maksimal ID transaksi yang bisa ditambahkan
        int remainingLength = 15 - partialLength;
        String transactionId = String.format("%010d", transaction.getId());

        // Jika panjang ID transaksi lebih besar dari sisa panjang yang dibutuhkan
        if (transactionId.length() > remainingLength) {
            transactionId = transactionId.substring(transactionId.length() - remainingLength);
        } else {
            // Tambahkan 0 di depan jika panjang ID transaksi kurang dari sisa panjang yang dibutuhkan
            transactionId = String.format("%0" + remainingLength + "d", transaction.getId());
        }

        return partialTicketNumber + transactionId;
    }


    @Override
    public TicketResponseDTO createNewTicket(Long id, TicketRequestDTO ticketRequestDTO) throws Exception {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Tickets latestTickets = new Tickets();
        if (!transaction.getTickets().isEmpty())
            latestTickets = transaction.getTickets().get(0);

        if (latestTickets.getTicketStatus() != TicketStatus.Selesai && latestTickets.getTicketStatus() != null)
            throw new BadRequestException("Request is not valid");

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
                .description(ticketRequestDTO.getComment())
                .divisionTarget(divisionTarget)
                .ticketStatus(TicketStatus.Diajukan)
                .admin(adminRepository.findByUsername("admin12"))
                .referenceNumber(latestTickets != null ? latestTickets.getTicketNumber() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);
        logger.info("Saved ticket: {}", savedTicket.getTicketNumber());
        createTicketHistory(savedTicket);

        if (savedTicket.getTicketCategory() == TicketCategories.Transfer && savedTicket.getReferenceNumber() == null) {
            checkProblem(transaction, savedTicket);
//            scheduler.schedule(() -> {
//                try {
//                    checkProblem(transaction, savedTicket);
//                } catch (Exception e) {
//                    throw new RuntimeException("Failed to check ticket");
//                }
//            }, 10, TimeUnit.SECONDS);
        } else {
            smartTicketService.updateTicketAdmin(savedTicket);
        }

        return TicketResponseDTO.builder()
                .transaction_id(transaction.getId())
                .account_number(transaction.getAccount().getAccountNumber())
                .ticket_category(savedTicket.getTicketCategory().toString())
                .reopen_ticket(savedTicket.getReferenceNumber() != null)
                .reference_number(savedTicket.getReferenceNumber() != null ? savedTicket.getReferenceNumber() : null)
                .build();
    }

    @Async
    private void checkProblem(Transaction transaction, Tickets tickets) throws Exception {
        try {
            Account accountRecipient = transaction.getRecipient_account();

            Specification<Transaction> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();

                Optional.of(transaction.getId()).ifPresent(id ->
                        predicates.add(builder.equal(root.get("referenced_id"), id))
                );

                Optional.of(transaction.getTransaction_type() == TransactionType.In ? TransactionType.Out : TransactionType.In).ifPresent(type ->
                        predicates.add(builder.equal(root.get("transaction_type"), type))
                );

                Optional.of(transaction.getAmount()).ifPresent(amount ->
                        predicates.add(builder.equal(root.get("amount"), amount))
                );

                return builder.and(predicates.toArray(new Predicate[0]));
            };

            List<Transaction> transactionRecipient = transactionRepository.findAll(spec);
            int recipientSize = transactionRecipient.size();

            if (recipientSize == 0) {
                processTicket(tickets, "IP {}, Transaction not found, Continuing ticket to division");
            } else if (recipientSize == 1) {
                finishingTicket(tickets, "IP {}, Transaction found, closing ticket");
                createMultipleTicketHistories(tickets, 3L, 4L, 5L);
            } else {
                processTicket(tickets, "IP {}, Transaction found, But need more check, Continuing ticket to division");
            }

        } catch (Exception e) {
            logger.warn("Failed to check problem in Transaction: {}", e.getMessage());
            throw new Exception("Failed to check problem, but ticket created");
        }
    }

    @Async
    private void processTicket(Tickets tickets, String logMessage) {
        updateTicketStatus(tickets, TicketStatus.DalamProses);
        smartTicketService.updateTicketAdmin(tickets);
        logger.info(TICKETS_MARKER, logMessage, loggerService.getClientIp());
    }

    @Async
    private void finishingTicket(Tickets tickets, String logMessage) {
        updateTicketStatus(tickets, TicketStatus.DalamProses);
        updateTicketStatus(tickets, TicketStatus.Selesai);
//        scheduler.schedule(() -> {
//            try {
//                updateTicketStatus(tickets, TicketStatus.Selesai);
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to update ticket selesai");
//            }
//        }, 20, TimeUnit.SECONDS);
        logger.info(TICKETS_MARKER, logMessage, loggerService.getClientIp());
    }

    private void updateTicketStatus(Tickets tickets, TicketStatus status) {
        tickets.setTicketStatus(status);
        Tickets savedTicket = ticketsRepository.save(tickets);
        // Send email notification
        try {
            emailService.sendNotification(savedTicket);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send notification");
        }
    }

    private void createMultipleTicketHistories(Tickets tickets, Long... historyIds) {
        for (Long historyId : historyIds) {
            createSingleTicketHistory(tickets, adminRepository.findByUsername("admin12"), getHistoryMessage(historyId), historyId);
        }
    }

    private String getHistoryMessage(Long historyId) {
        switch (historyId.intValue()) {
            case 3:
                return "laporan dalam proses";
            case 4:
                return "laporan selesai diproses";
            case 5:
                return "laporan diterima pelapor";
            default:
                return "laporan diperbarui";
        }
    }

    @Async
    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin12");
        logger.info("admin : {} {}", admin.getFirstName(), admin.getLastName());
        createSingleTicketHistory(ticket, admin, "transaksi dilakukan", 1L);
        createSingleTicketHistory(ticket, admin, "laporan diajukan", 2L);
    }

    @Async
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
            Transaction transaction = transactionRepository.findById(id).get();
            logger.info(TICKETS_MARKER, "IP {}, Transaction Detail acquired for Form Complaint", loggerService.getClientIp());
            return ComplaintResponseDTO.builder()
                    .transaction_id(transaction.getId())
                    .account_number(transaction.getAccount().getAccountNumber())
                    .ticket_category(transaction.getCategory().toString())
                    .reopen_ticket(transaction.getTickets() != null)
                    .reference_number(transaction.getTickets().isEmpty() ? null : transaction.getTickets().get(0).getTicketNumber())
                    .build();
        } catch (IndexOutOfBoundsException e) {
            logger.error(TICKETS_MARKER, "IP {}, List Out of Bounds", loggerService.getClientIp(), e);
            throw new IndexOutOfBoundsException("List Tickets not found");
        } catch (EntityNotFoundException e) {
            logger.error(TICKETS_MARKER, "IP {}, Transaction Not found", loggerService.getClientIp(), e);
            throw new EntityNotFoundException("Transaction Not found");
        } catch (Exception e) {
            logger.error(TICKETS_MARKER, "IP {}, Error getting form complaint", loggerService.getClientIp(), e);
            throw new Exception("Error getting customer ticket details");
        }
    }

    @Override
    public TicketFeedbackResponseDTO getTicketFeedback(Long ticket_id) {
        Tickets ticket = ticketsRepository.findById(ticket_id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return TicketFeedbackResponseDTO.builder()
                .rating((ticket.getTicketFeedback() == null) ? 0 : (ticket.getTicketFeedback().getStar_rating() == null ? 0 :
                        switch (ticket.getTicketFeedback().getStar_rating()) {
                            case Satu -> 1;
                            case Dua -> 2;
                            case Tiga -> 3;
                            case Empat -> 4;
                            case Lima -> 5;
                        }))
                .comment(ticket.getTicketFeedback() == null ? null : (ticket.getTicketFeedback().getComment()))
                .build();
    }

    @Override
    public CustomerTicketFeedbackResponseDTO getCustomerTicketFeedback(String ticket_number) {
        Tickets ticket = ticketsRepository.findByTicketNumber(ticket_number);
        if (ticket == null) throw new RuntimeException("Ticket not found");

        return CustomerTicketFeedbackResponseDTO.builder()
                .rating((ticket.getTicketFeedback() == null) ? 0 : (ticket.getTicketFeedback().getStar_rating() == null ? 0 :
                        switch (ticket.getTicketFeedback().getStar_rating()) {
                            case Satu -> 1;
                            case Dua -> 2;
                            case Tiga -> 3;
                            case Empat -> 4;
                            case Lima -> 5;
                        }))
                .build();
    }

    @Override
    public CreateFeedbackResponseDTO createFeedback(
            CreateFeedbackRequestDTO requestDTO,
            String ticket_number,
            Authentication authentication) {

        String actualTicketNumber = ticket_number != null ? ticket_number : requestDTO.getTicket_number();

        Tickets ticket = ticketsRepository.findByTicketNumber(actualTicketNumber);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        TicketFeedback existingFeedback = ticketFeedbackRepository.findByTicket(ticket)
                .orElseGet(() -> {
                    TicketFeedback feedback = new TicketFeedback();
                    feedback.setTicket(ticket);
                    feedback.setStar_rating(requestDTO.getRating() != 0 ? StarRating.fromValue(requestDTO.getRating()) : null); // Set default rating if null
                    feedback.setComment(requestDTO.getComment());
                    feedback.setCreatedAt(LocalDateTime.now());
                    feedback.setUpdatedAt(LocalDateTime.now());
                    ticketFeedbackRepository.save(feedback);
                    return feedback;
                });

        existingFeedback.setStar_rating(switch (requestDTO.getRating()) {
            case 1 -> StarRating.Satu;
            case 2 -> StarRating.Dua;
            case 3 -> StarRating.Tiga;
            case 4 -> StarRating.Empat;
            case 5 -> StarRating.Lima;
            default -> null;
        });
        existingFeedback.setComment(requestDTO.getComment() != null ? requestDTO.getComment() : (existingFeedback.getComment() == null ? null : existingFeedback.getComment()));
        existingFeedback.setUpdatedAt(LocalDateTime.now());

        ticketFeedbackRepository.save(existingFeedback);

        return CreateFeedbackResponseDTO.builder()
                .result(CreateFeedbackResponseDTO.FeedbackDetails.builder()
                        .rating(existingFeedback.getStar_rating().getValue())
                        .comment(existingFeedback.getComment())
                        .ticket_number(existingFeedback.getTicket().getTicketNumber())
                        .createdAt(existingFeedback.getCreatedAt())
                        .updatedAt(existingFeedback.getUpdatedAt())
                        .build())
                .build();
    }
}
