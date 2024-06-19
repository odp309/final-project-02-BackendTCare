package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.tickets.ListTicketNasabahResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketsNasabahResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketsResponseDTO;
import com.bni.finproajubackend.interfaces.TicketReportsInterface;
import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketReportsService implements TicketReportsInterface {

    private static final Logger logger = LoggerFactory.getLogger(TicketReportsService.class);
    private static final Marker TICKET_MARKER = MarkerFactory.getMarker("TICKET");

    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggerService loggerService;

    @Override
    public PaginationDTO getAllTickets(
            String user,
            @RequestParam(required = false) String account_number,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(required = false) String created_at,
            @RequestParam(required = false) String division,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "created_at") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order,
            Authentication authentication
    ) throws IllegalAccessException {
        try {
            return switch (user.toLowerCase()) {
                case "admin" ->
                        adminTicketReports(category, rating, status, start_date, end_date, ticket_number, created_at, division, page, limit, sort_by, order, authentication);
                case "customer" -> nasabahTicketReports(account_number, status, sort_by, order, authentication);
                default -> throw new IllegalArgumentException("Path is not valid");
            };
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Failed getting data");
        }
    }

    private PaginationDTO nasabahTicketReports(
            String account_number,
            String status,
            String sort_by,
            String order,
            Authentication authentication
    ) throws IllegalAccessException {
        if (account_number == null) throw new IllegalArgumentException("Account number cannot be empty");
        Account account = accountRepository.findByAccountNumber(account_number);
        if (account == null) throw new NotFoundException("Account not found");
        if (!account.getNasabah().getUser().getUsername().equals(authentication.getName()))
            throw new IllegalAccessException("User is not the owner");

        Sort.Direction sortDirection = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        List<Sort.Order> orders = List.of(new Sort.Order(sortDirection, sort_by));

        Specification<Tickets> spec = getSpec(account_number, null, null, status, null, null, null, null, null);

        Pageable pageable = PageRequest.of(0, ticketsRepository.findAll().size(), Sort.by(orders));
        Page<Tickets> ticketsPage = ticketsRepository.findAll(spec, pageable);

        List<TicketsNasabahResponseDTO> ticketsResponseDTOList = ticketsPage.getContent().stream()
                .map(ticket -> TicketsNasabahResponseDTO.builder()
                        .id(ticket.getId())
                        .transaction_id(ticket.getTransaction().getId())
                        .ticket_number(ticket.getTicketNumber())
                        .transaction_type(ticket.getTransaction().getTransaction_type().toString())
                        .ticket_date(ticket.getCreatedAt().toString())
                        .amount(ticket.getTransaction().getAmount())
                        .ticket_description(ticket.getDescription())
                        .ticket_status(ticket.getTicketStatus().toString())
                        .build())
                .collect(Collectors.toList());

        if (ticketsResponseDTOList.isEmpty()) return null;

        ListTicketNasabahResponseDTO listTicketNasabahResponseDTO = ListTicketNasabahResponseDTO.builder()
                .account_number(account_number)
                .list_tickets(ticketsResponseDTOList)
                .build();

        logger.info(TICKET_MARKER, "IP {}, List Ticket for Nasabah {}", loggerService.getClientIp(), authentication.getName());

        return PaginationDTO.builder()
                .data(listTicketNasabahResponseDTO)
                .currentPage(ticketsPage.getNumber())
                .currentItem(ticketsPage.getNumberOfElements())
                .totalPage(ticketsPage.getTotalPages())
                .totalItem(ticketsPage.getTotalElements())
                .build();
    }

    private PaginationDTO adminTicketReports(
            String category,
            Integer rating,
            String status,
            String start_date,
            String end_date,
            String ticket_number,
            String created_at,
            String division,
            int page,
            int limit,
            String sort_by,
            String order,
            Authentication authentication
    ) throws IllegalAccessException {
        Admin admin = userRepository.findByUsername(authentication.getName()).getAdmin();
        if (admin == null || !admin.getRole().getRoleName().equals("admin"))
            throw new IllegalAccessException("Not Admin");

        sort_by = convertSortBy(sort_by);
        Sort.Direction sortDirection = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "ticketStatus"));
        orders.add(new Sort.Order(sortDirection, sort_by));
        Pageable pageable = PageRequest.of(page, limit, Sort.by(orders));

        Specification<Tickets> spec = getSpec(null, category, rating, status, start_date, end_date, ticket_number, created_at, division);

        Page<Tickets> ticketsPage = ticketsRepository.findAll(spec, pageable);

        if (ticketsPage.isEmpty()) {
            List<Tickets> allTickets = ticketsRepository.findAll(spec, Sort.by(sortDirection, sort_by));
            ticketsPage = new PageImpl<>(allTickets);
        }

        List<TicketsResponseDTO> ticketsResponseDTOList = ticketsPage.getContent().stream()
                .map(ticket -> TicketsResponseDTO.builder()
                        .id(ticket.getId())
                        .transaction_id(ticket.getTransaction().getId())
                        .ticket_number(ticket.getTicketNumber())
                        .ticket_category(ticket.getTicketCategory())
                        .category(switch (ticket.getTicketCategory()) {
                            case Transfer -> "Gagal Transfer";
                            case TopUp -> "Gagal TopUp";
                            case Payment -> "Gagal Payment";
                            default -> null;
                        })
                        .time_response(ticket.getTicketResponseTime() == null ? 0 : ticket.getTicketResponseTime().getResponseTime())
                        .status(switch (ticket.getTicketStatus()) {
                            case Diajukan -> "Diajukan";
                            case DalamProses -> "Dalam Proses";
                            case Selesai -> "Selesai";
                        })
                        .division_target(ticket.getDivisionTarget())
                        .rating(ticket.getTicketFeedback() == null ? 4 : ticket.getTicketFeedback().getStar_rating().getValue())
                        .created_at(ticket.getCreatedAt())
                        .updated_at(ticket.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        if (ticketsResponseDTOList.isEmpty()) return null;

        logger.info(TICKET_MARKER, "IP {}, Ticket Response List", loggerService.getClientIp());

        return PaginationDTO.builder()
                .data(ticketsResponseDTOList)
                .currentPage(ticketsPage.getNumber())
                .currentItem(ticketsPage.getNumberOfElements())
                .totalPage(ticketsPage.getTotalPages())
                .totalItem(ticketsPage.getTotalElements())
                .build();
    }

    private String convertSortBy(String sort_by) {
        return switch (sort_by.toLowerCase()) {
            case "ticket_number" -> "ticketNumber";
            case "created_at" -> "createdAt";
            case "status" -> "ticketStatus";
            default -> sort_by;
        };
    }

    private Specification<Tickets> getSpec(
            String account_number,
            String category,
            Integer rating,
            String status,
            String start_date,
            String end_date,
            String ticket_number,
            String created_at,
            String division
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(category).ifPresent(cat -> {
                TicketCategories ticketCategoryEnum = switch (cat.toLowerCase()) {
                    case "gagal transfer" -> TicketCategories.Transfer;
                    case "gagal topup" -> TicketCategories.TopUp;
                    case "gagal payment" -> TicketCategories.Payment;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
            });

            Optional.ofNullable(rating).ifPresent(r -> {
                Join<Tickets, TicketFeedback> feedbackJoin = root.join("ticketFeedbacks", JoinType.LEFT);
                if (r == 4) {
                    predicates.add(
                            builder.or(
                                    builder.isNull(feedbackJoin.get("starRating")),
                                    builder.equal(feedbackJoin.get("starRating"), StarRating.Empat)
                            )
                    );
                } else {
                    predicates.add(builder.equal(feedbackJoin.get("starRating"), StarRating.fromValue(r)));
                }
            });

            Optional.ofNullable(status).ifPresent(st -> {
                TicketStatus ticketStatusEnum = switch (st.toLowerCase()) {
                    case "diajukan" -> TicketStatus.Diajukan;
                    case "dalam proses" -> TicketStatus.DalamProses;
                    case "selesai" -> TicketStatus.Selesai;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketStatus"), ticketStatusEnum));
            });

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            Optional.ofNullable(start_date).map(date -> LocalDateTime.parse(date + "T00:00:00", formatter))
                    .ifPresent(startDate -> Optional.ofNullable(end_date).map(date -> LocalDateTime.parse(date + "T23:59:59", formatter))
                            .ifPresent(endDate -> predicates.add(builder.between(root.get("createdAt"), startDate, endDate))));

            Optional.ofNullable(created_at).map(LocalDate::parse)
                    .ifPresent(date -> predicates.add(builder.equal(root.get("createdAt").as(LocalDate.class), date)));

            Optional.ofNullable(ticket_number).ifPresent(tn -> predicates.add(builder.like(root.get("ticketNumber"), "%" + tn + "%")));

            Optional.ofNullable(account_number).ifPresent(acc -> predicates.add(builder.equal(root.get("transaction").get("account").get("accountNumber"), acc)));

            Optional.ofNullable(division).ifPresent(acc -> {
                DivisionTarget divisionTarget = switch (acc.toLowerCase()) {
                    case "dgo" -> DivisionTarget.DGO;
                    case "cxc" -> DivisionTarget.CXC;
                    case "wpp" -> DivisionTarget.WPP;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("divisionTarget"), divisionTarget));
            });

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
