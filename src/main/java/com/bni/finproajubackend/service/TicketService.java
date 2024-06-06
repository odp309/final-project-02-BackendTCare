package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.criteria.Predicate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class TicketService implements TicketInterface {

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
    public Tickets updateTicketStatus(Long ticketId, TicketStatus status, Authentication authentication) {
        Tickets ticket = ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        TicketStatus oldStatus = ticket.getTicketStatus();
        ticket.setTicketStatus(status);
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
        ticketHistory.setDescription("Ticket status updated from " + oldStatus + " to " + status);
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(statusLevel);
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());
        ticketHistoryRepository.save(ticketHistory);

        // Handle TicketResponseTime when status is Ditutup
        if (status == TicketStatus.Ditutup) {
            handleTicketResponseTime(ticket);
        }

        // Send email notification
        emailService.sendNotification(ticket);

        return ticket;
    }

    private Long getStatusLevel(TicketStatus status) {
        return status == TicketStatus.Dibuat ? 1L :
                status == TicketStatus.Diajukan ? 2L :
                        status == TicketStatus.DalamProses ? 3L :
                                status == TicketStatus.Selesai ? 4L : 5L;
    }

    private void handleTicketResponseTime(Tickets ticket) {
        TicketResponseTime responseTime = new TicketResponseTime();
        responseTime.setTicket(ticket);

        LocalDate createdAt = ticket.getCreatedAt().toLocalDate();
        LocalDate closedAt = LocalDate.now();
        long daysBetween = java.time.Duration.between(createdAt.atStartOfDay(), closedAt.atStartOfDay()).toDays();

        responseTime.setResponseTime(daysBetween);
        responseTime.setCreatedAt(LocalDateTime.now());
        responseTime.setUpdatedAt(LocalDateTime.now());

        ticketResponseTimeRepository.save(responseTime);
    }

    @Override
    public TicketResponseDTO getTicketDetails(String ticketNumber) {
        Tickets ticket = ticketsRepository.findByTicket(ticketNumber);
        if (ticket == null) {
            return null;
        }

        return TicketResponseDTO.builder()
                .ticketNumber(ticket.getTicketNumber())
                .ticketCategory(ticket.getTicketCategory())
                .createdAt(ticket.getCreatedAt())
                .transaction(ticket.getTransaction())
                .status(ticket.getTicketStatus())
                .description(ticket.getDescription())
                .build();
    }

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
    public PaginationDTO<TicketResponseDTO> getAllTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "created_at") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        // Build pageable object for pagination
        Pageable pageable = PageRequest.of(page, limit);

        // Convert start_date and end_date to LocalDateTime objects
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (start_date != null && end_date != null) {
            startDate = LocalDateTime.parse(start_date + "T00:00:00");
            endDate = LocalDateTime.parse(end_date + "T23:59:59");
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be bigger than or equal to start date");
            }
        } else {
            endDate = null;
            startDate = null;
        }

        // Create Specification for filtering
        Specification<Tickets> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by category
            if (category != null) {
                // Convert String category to TicketCategories enum
                TicketCategories ticketCategoryEnum = switch (category){
                    case "\"Gagal Transfer\"" -> TicketCategories.Transfer;
                    case "\"Gagal TopUp\"" -> TicketCategories.TopUp;
                    case "\"Gagal Payment\"" -> TicketCategories.Payment;
                    default -> null;
                };

                predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
            }

            // Filter by rating
            if (rating != null) {
                // Assuming rating is a field in Tickets entity
                predicates.add(builder.equal(root.get("rating"), rating));
            }

            // Filter by status
            if (status != null) {
                // Assuming status is a field in Tickets entity
                predicates.add(builder.equal(root.get("status"), status));
            }

            // Filter by ticket created at
            if (startDate != null && endDate != null) {
                predicates.add(builder.between(root.get("createdAt"), startDate, endDate));
            }

            // Search by ticket number
            if (ticket_number != null) {
                predicates.add(builder.equal(root.get("ticketNumber"), ticket_number));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        // Perform query with Specification and pageable
        Page<Tickets> ticketsPage = ticketsRepository.findAll(spec, pageable);

        // Convert Page<Tickets> to List<TicketResponseDTO>
        List<TicketResponseDTO> ticketResponseDTOList = ticketsPage.getContent().stream()
                .map(ticket -> TicketResponseDTO.builder()
                        .ticketNumber(ticket.getTicketNumber())
                        .transaction(ticket.getTransaction())
                        .ticketCategory(ticket.getTicketCategory())
                        .description(ticket.getDescription())
                        .createdAt(ticket.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // Create PaginationDTO

        return PaginationDTO.<TicketResponseDTO>builder()
                .result(ticketResponseDTOList)
                .currentPage(ticketsPage.getNumber())
                .currentItem(ticketsPage.getNumberOfElements())
                .totalPage(ticketsPage.getTotalPages())
                .totalItem(ticketsPage.getTotalElements())
                .build();
    }

    @Override
    public TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO) {
        Transaction transaction = transactionRepository.findById(ticketRequestDTO.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Tickets ticket = Tickets.builder()
                .ticketNumber(createTicketNumber(transaction))
                .transaction(transaction)
                .ticketCategory(ticketRequestDTO.getTicketCategory())
                .description(ticketRequestDTO.getDescription())
                .ticketStatus(TicketStatus.Dibuat)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);

        return TicketResponseDTO.builder()
                .ticketNumber(savedTicket.getTicketNumber())
                .transaction(savedTicket.getTransaction())
                .ticketCategory(savedTicket.getTicketCategory())
                .description(savedTicket.getDescription())
                .createdAt(savedTicket.getCreatedAt())
                .build();
    }

    public String getAdminFullName(@NotNull Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }
}
