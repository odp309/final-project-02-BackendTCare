package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.PaginationDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.criteria.Predicate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

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
    public TicketResponseDTO getTicketDetails(String ticketNumber) {
        Tickets ticket = ticketsRepository.findByTicketNumber(ticketNumber);
        if (ticket == null)
            throw new EntityNotFoundException("Ticket not found");

        TicketResponseDTO responseDTO = TicketResponseDTO.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .ticketCategory(ticket.getTicketCategory())
                .status(ticket.getTicketStatus())
                .description(ticket.getDescription())
                .referenceNumber(ticket.getReferenceNumber())
                .report_date(ticket.getCreatedAt())
                .created_at(ticket.getCreatedAt())
                .updated_at(ticket.getUpdatedAt())
                .build();

        return responseDTO;
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
    public PaginationDTO<TicketResponseDTO> getAllTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "createdAt") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {

        // Determine sort direction
        Sort.Direction sortDirection = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        if (sort_by.equalsIgnoreCase("ticket_number"))
            sort_by = "ticketNumber";

        // Build pageable object for pagination
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sort_by));

        // Define date formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Convert start_date and end_date to LocalDateTime objects
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (start_date != null && end_date != null) {
            start_date = start_date.replace("”", "");
            end_date = end_date.replace("”", "");
            startDate = LocalDateTime.parse(start_date + "T00:00:00", formatter);
            endDate = LocalDateTime.parse(end_date + "T23:59:59", formatter);
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
                logger.info("Category Trigger");
                // Convert String category to TicketCategories enum
                TicketCategories ticketCategoryEnum = switch (category.toLowerCase()) {
                    case "\"gagal transfer\"" -> TicketCategories.Transfer;
                    case "\"gagal topup\"" -> TicketCategories.TopUp;
                    case "\"gagal payment\"" -> TicketCategories.Payment;
                    default -> null;
                };
                logger.info(ticketCategoryEnum.toString());

                predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
            }

            // Filter by rating
            if (rating != null) {
                logger.info("Rating Trigger");
                // Assuming rating is a field in Tickets entity
                predicates.add(builder.equal(root.get("rating"), rating));
            }

            // Filter by status
            if (status != null) {
                logger.info("Status Trigger");
                // Assuming status is a field in Tickets entity
                predicates.add(builder.equal(root.get("status"), status));
            }

            // Filter by ticket created at
            if (startDate != null && endDate != null) {
                logger.info("Date Trigger");
                predicates.add(builder.between(root.get("createdAt"), startDate, endDate));
            }

            // Search by ticket number
            if (ticket_number != null) {
                logger.info("Ticket Number Trigger");
                predicates.add(builder.equal(root.get("ticketNumber"), ticket_number));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        // Perform query with Specification and pageable
        Page<Tickets> ticketsPage = ticketsRepository.findAll(spec, pageable);
        logger.info("Tickets Page Created");

        // Convert Page<Tickets> to List<TicketResponseDTO>
        List<TicketResponseDTO> ticketResponseDTOList = ticketsPage.getContent().stream()
                .map(ticket -> TicketResponseDTO.builder()
                        .id(ticket.getId())
                        .ticketNumber(ticket.getTicketNumber())
                        .ticketCategory(ticket.getTicketCategory())
                        .ticket_number(ticket.getTicketNumber())
                        .category(switch (ticket.getTicketCategory()) {
                            case Transfer -> "Gagal Transfer";
                            case TopUp -> "Gagal TopUp";
                            case Payment -> "Gagal Payment";
                            default -> null;
                        })
                        .time_response(ticket.getTicketResponseTime() == null ? 0 : ticket.getTicketResponseTime().getResponseTime())
                        .status(ticket.getTicketStatus())
                        .division_target(ticket.getDivisionTarget())
                        .rating(switch (ticket.getTicketFeedbacks() == null ? StarRating.Empat : ticket.getTicketFeedbacks().getStarRating()) {
                            case Satu -> 1;
                            case Dua -> 2;
                            case Tiga -> 3;
                            case Lima -> 5;
                            default -> 4;
                        })
                        .created_at(ticket.getCreatedAt())
                        .updated_at(ticket.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        if (ticketResponseDTOList.isEmpty())
            return null;
        // Create PaginationDTO
        logger.info("Ticket Response Created");

        return PaginationDTO.<TicketResponseDTO>builder()
                .data(ticketResponseDTOList)
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
                .description(ticketRequestDTO.getDescription())
                .divisionTarget(divisiTarget)
                .ticketStatus(TicketStatus.Diajukan)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);

        // Load ticket history
        createTicketHistory(savedTicket);

        return TicketResponseDTO.builder()
                .id(savedTicket.getId())
                .ticketCategory(savedTicket.getTicketCategory())
                .ticket_number(savedTicket.getTicketNumber())
                .category(switch (ticket.getTicketCategory()) {
                    case Transfer -> "Gagal Transfer";
                    case TopUp -> "Gagal TopUp";
                    case Payment -> "Gagal Payang";
                    default -> null;
                })
                .time_response(savedTicket.getTicketResponseTime() == null ? 0 : savedTicket.getTicketResponseTime().getResponseTime())
                .status(savedTicket.getTicketStatus())
                .division_target(savedTicket.getDivisionTarget())
                .rating(switch (savedTicket.getTicketFeedbacks() == null ? StarRating.Empat : savedTicket.getTicketFeedbacks().getStarRating()) {
                    case Satu -> 1;
                    case Dua -> 2;
                    case Tiga -> 3;
                    case Lima -> 5;
                    default -> 4;
                })
                .created_at(savedTicket.getCreatedAt())
                .updated_at(savedTicket.getCreatedAt())
                .build();
    }

    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin12");

        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription("Laporan " + ticket.getTicketStatus());
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(1L); // Assuming level 1 for ticket creation
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());

        ticketHistoryRepository.save(ticketHistory);
    }

    public String getAdminFullName(@NotNull Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }
}
