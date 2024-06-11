package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.*;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

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

    @Transactional
    public Tickets updateTicketStatus(Long ticketId, Authentication authentication) {
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
    public String createTicketNumber(Transaction transaction) {
        String categoryCode = switch (transaction.getCategory()) {
            case Transfer -> "TF"; // ID kategori 1 untuk Gagal Transfer
            case TopUp -> "TU"; // ID kategori 2 untuk Gagal Top Up
            case Payment -> "PY"; // ID kategori 3 untuk Gagal Pembayaran
            default -> ""; // ID kategori tidak valid
        };

        LocalDateTime createdAt = transaction.getCreatedAt();
        String year = String.valueOf(createdAt.getYear());
        String month = String.format("%02d", createdAt.getMonthValue());
        String day = String.format("%02d", createdAt.getDayOfMonth());

        String transactionId = String.valueOf(transaction.getId());
        String baseTicketNumber = categoryCode + year + month + day + transactionId;

        if (baseTicketNumber.length() < 15) {
            int zerosToAdd = 15 - baseTicketNumber.length();
            transactionId = "0".repeat(zerosToAdd) + transactionId;
        }

        return categoryCode + year + month + day + transactionId;
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
    public TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO) {
        Transaction transaction = transactionRepository.findById(ticketRequestDTO.getTransaction_id())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        TicketCategories categories = transaction.getCategory() == TransactionCategories.Payment ? TicketCategories.Payment
                : transaction.getCategory() == TransactionCategories.TopUp ? TicketCategories.TopUp
                : TicketCategories.Transfer;

        DivisionTarget divisiTarget = transaction.getCategory() == TransactionCategories.Payment ? DivisionTarget.DGO
                : transaction.getCategory() == TransactionCategories.TopUp ? DivisionTarget.WPP
                : DivisionTarget.CXC;

        Tickets ticket = Tickets.builder()
                .ticketNumber(createTicketNumber(transaction))
                .transaction(transaction)
                .ticketCategory(categories)
                .description("Complaint Ticket")
                .divisionTarget(divisiTarget)
                .ticketStatus(TicketStatus.Diajukan)
                .referenceNumber(ticketRequestDTO.isReopen_ticket() ? ticketRequestDTO.getReference_number() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);

        // Load ticket history
        createTicketHistory(savedTicket);

        return TicketResponseDTO.builder()
                .id(savedTicket.getId())
                .ticket_category(savedTicket.getTicketCategory())
                .ticket_number(savedTicket.getTicketNumber())
                .category(switch (savedTicket.getTicketCategory()) {
                    case Transfer -> "Gagal Transfer";
                    case TopUp -> "Gagal TopUp";
                    case Payment -> "Gagal Payang";
                    default -> null;
                })
                .time_response(savedTicket.getTicketResponseTime() == null ? 0 : savedTicket.getTicketResponseTime().getResponseTime())
                .status(switch (savedTicket.getTicketStatus()) {
                    case Diajukan -> "Diajukan";
                    case DalamProses -> "Dalam Proses";
                    case Selesai -> "Selesai";
                })
                .division_target(savedTicket.getDivisionTarget())
                .rating(switch (savedTicket.getTicketFeedbacks() == null ? StarRating.Empat : savedTicket.getTicketFeedbacks().getStarRating()) {
                    case Satu -> 1;
                    case Dua -> 2;
                    case Tiga -> 3;
                    case Lima -> 5;
                    default -> 4;
                })
                .transaction(savedTicket.getTransaction())
                .description("Complaint Form")
                .reference_number(savedTicket.getReferenceNumber())
                .report_date(savedTicket.getCreatedAt())
                .created_at(savedTicket.getCreatedAt())
                .updated_at(savedTicket.getCreatedAt())
                .build();
    }

    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin12");

        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription("Laporan Dibukan");
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(1L); // Assuming level 1 for ticket creation
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());

        ticketHistoryRepository.save(ticketHistory);

        TicketHistory ticketHistory2 = new TicketHistory();

        ticketHistory2.setTicket(ticket);
        ticketHistory2.setAdmin(admin);
        ticketHistory2.setDescription("Laporan " + ticket.getTicketStatus());
        ticketHistory2.setDate(new Date());
        ticketHistory2.setLevel(2L); // Assuming level 1 for ticket creation
        ticketHistory2.setCreatedAt(LocalDateTime.now());
        ticketHistory2.setUpdatedAt(LocalDateTime.now());

        ticketHistoryRepository.save(ticketHistory2);
    }

    public String getAdminFullName(@NotNull Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }


}
