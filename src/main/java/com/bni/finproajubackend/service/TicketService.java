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
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    private TicketFeedbackRepository ticketFeedbackRepository;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;

    //    @Transactional
//    public Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException {
//        Tickets ticket = ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
//        TicketStatus oldStatus = ticket.getTicketStatus();
//        if (oldStatus != TicketStatus.Selesai) {
//            TicketStatus nextStatus = oldStatus == TicketStatus.Diajukan ? TicketStatus.DalamProses
//                    : TicketStatus.Selesai;
//
//            ticket.setTicketStatus(nextStatus);
//            ticketsRepository.save(ticket);
//
//            // Get admin details from authentication
//            String username = authentication.getName();
//            Admin admin = adminRepository.findByUsername(username);
//
//            if (admin == null)
//                throw new RuntimeException("User not found");
//
//            Long statusLevel = getStatusLevel(ticket.getTicketStatus());
//
//            // Create a new ticket history entry
//            TicketHistory ticketHistory = new TicketHistory();
//            ticketHistory.setTicket(ticket);
//            ticketHistory.setAdmin(admin);
//            ticketHistory.setDescription(nextStatus == TicketStatus.DalamProses ? "laporan dalam proses" :
//                    nextStatus == TicketStatus.Selesai ? "laporan selesai diproses" : "");
//            ticketHistory.setDate(new Date());
//            ticketHistory.setLevel(statusLevel);
//            ticketHistory.setCreatedAt(LocalDateTime.now());
//            ticketHistory.setUpdatedAt(LocalDateTime.now());
//            ticketHistoryRepository.save(ticketHistory);
//
//            // Send email notification
//            emailService.sendNotification(ticket);
//
//            if(nextStatus == TicketStatus.DalamProses){
//                ticketHistory.setTicket(ticket);
//                ticketHistory.setAdmin(admin);
//                ticketHistory.setDescription("laporan dalam proses");
//                ticketHistory.setDate(new Date());
//                ticketHistory.setLevel(statusLevel);
//                ticketHistory.setCreatedAt(LocalDateTime.now());
//                ticketHistory.setUpdatedAt(LocalDateTime.now());
//                ticketHistoryRepository.save(ticketHistory);
//            }
//
//            if(nextStatus == TicketStatus.Selesai){
//                ticketHistory.setTicket(ticket);
//                ticketHistory.setAdmin(admin);
//                ticketHistory.setDescription("laporan selesai diproses");
//                ticketHistory.setDate(new Date());
//                ticketHistory.setLevel(statusLevel);
//                ticketHistory.setCreatedAt(LocalDateTime.now());
//                ticketHistory.setUpdatedAt(LocalDateTime.now());
//                ticketHistoryRepository.save(ticketHistory);
//
//                ticketHistory.setTicket(ticket);
//                ticketHistory.setAdmin(admin);
//                ticketHistory.setDescription("laporan diterima pelapor");
//                ticketHistory.setDate(new Date());
//                ticketHistory.setLevel(statusLevel);
//                ticketHistory.setCreatedAt(LocalDateTime.now());
//                ticketHistory.setUpdatedAt(LocalDateTime.now());
//                ticketHistoryRepository.save(ticketHistory);
//            }
//
//            // Handle TicketResponseTime when status is Ditutup
//            if (nextStatus == TicketStatus.Selesai) {
//                handleTicketResponseTime(ticket);
//            }
//        }
//        return ticket;
//    }
    @Transactional
    public Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException {
        Tickets ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
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
                                .report_date(ticket.getCreatedAt())
                                .ticket_number(ticket.getTicketNumber())
                                .status(switch (ticket.getTicketStatus()) {
                                    case Diajukan -> "Diajukan";
                                    case DalamProses -> "Dalam Proses";
                                    case Selesai -> "Selesai";
                                })
                                .isReopened(ticket_reference != null)
                                .next_reference_num(ticket_reference != null ? ticket_reference.getTicketNumber() : null)
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
                .admin(adminRepository.findByUsername("admin12"))
                .referenceNumber(ticketRequestDTO.isReopen_ticket() ? ticketRequestDTO.getReference_number() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Tickets savedTicket = ticketsRepository.save(ticket);
        logger.info("Saved ticket: {}", savedTicket);
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
            createSingleTicketHistory(transaction.getTickets().get(0), adminRepository.findByUsername("system"), "laporan selesai diproses", 4L);
            createSingleTicketHistory(transaction.getTickets().get(0), adminRepository.findByUsername("system"), "laporan diterima pelapor", 5L);
        }
    }

    private void processTicket(Transaction transaction, String logMessage) {
        createSingleTicketHistory(transaction.getTickets().get(0), adminRepository.findByUsername("system"), "laporan dalam proses", 3L);
        logger.info(TICKETS_MARKER, logMessage, loggerService.getClientIp());
    }


    private void createTicketHistory(Tickets ticket) {
        Admin admin = adminRepository.findByUsername("admin12");
        logger.info("admin : {}", admin);
        createSingleTicketHistory(ticket, admin, "transaksi dilakukan", 1L);
        createSingleTicketHistory(ticket, admin, "laporan diajukan", 2L);
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
                    .reference_number(transaction.getTickets().isEmpty() ? null : transaction.getTickets().get(0).getTicketNumber())
                    .build();
        } catch (IndexOutOfBoundsException e) {
            logger.error(TICKETS_MARKER, "IP {}, List Out of Bounds", loggerService.getClientIp(), e);
            throw new Exception("List Tickets not found");
        } catch (EntityNotFoundException e) {
            logger.error(TICKETS_MARKER, "IP {}, Transaction Not Found", loggerService.getClientIp(), e);
            throw new Exception("Transaction Not Found");
        } catch (Exception e) {
            logger.error(TICKETS_MARKER, "IP {}, Error getting form complaint", loggerService.getClientIp(), e);
            throw new Exception("Error getting customer ticket details");
        }
    }

    @Override
    public TicketFeedbackResponseDTO getTicketFeedback(Long ticket_id) {
        TicketFeedback ticketFeedback = ticketFeedbackRepository.findByTicketId(ticket_id)
                .orElseGet(() -> {
                    Tickets ticket = ticketsRepository.findById(ticket_id)
                            .orElseThrow(() -> new RuntimeException("Ticket not found"));
                    TicketFeedback newFeedback = new TicketFeedback();
                    newFeedback.setTicket(ticket);
                    newFeedback.setStar_rating(null);
                    newFeedback.setComment("");
                    return ticketFeedbackRepository.save(newFeedback);
                });

        int rating = switch (ticketFeedback.getStar_rating()) {
            case Satu -> 1;
            case Dua -> 2;
            case Tiga -> 3;
            case Empat -> 4;
            case Lima -> 5;
            default -> 0;
        };

        return TicketFeedbackResponseDTO.builder()
                .rating(rating == 0 ? null : rating)
                .comment(ticketFeedback.getComment())
                .build();
    }

    @Override
    public CustomerTicketFeedbackResponseDTO getCustomerTicketFeedback(String ticket_number) {
        Tickets ticket = ticketsRepository.findByTicketNumber(ticket_number);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        TicketFeedback ticketFeedback = ticketFeedbackRepository.findByTicket(ticket).orElseGet(() -> {
            TicketFeedback tFeed = new TicketFeedback();
            tFeed.setTicket(ticket);
            tFeed.setCreatedAt(LocalDateTime.now());
            tFeed.setUpdatedAt(LocalDateTime.now());
            tFeed.setStar_rating(StarRating.Empat);
            return ticketFeedbackRepository.save(tFeed);
        });

        StarRating starRating = ticketFeedback.getStar_rating();
        int rating;
        if (starRating == null) {
            rating = 0;  // Default to 0 if starRating is null
        } else {
            rating = switch (starRating) {
                case Satu -> 1;
                case Dua -> 2;
                case Tiga -> 3;
                case Empat -> 4;
                case Lima -> 5;
                default -> 0;
            };
        }

        return CustomerTicketFeedbackResponseDTO.builder()
                .rating(rating)
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
                .orElseThrow(() -> new RuntimeException("Feedback for this ticket already exists"));

        TicketFeedback feedback = new TicketFeedback();
        feedback.setTicket(ticket);
        feedback.setStar_rating(requestDTO.getRating() != 0 ? StarRating.fromValue(requestDTO.getRating()) : null); // Set default rating if null
        feedback.setComment(requestDTO.getComment());
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());

        ticketFeedbackRepository.save(feedback);

        CreateFeedbackResponseDTO.FeedbackDetails feedbackDetails = new CreateFeedbackResponseDTO.FeedbackDetails();
        feedbackDetails.setTicket_number(actualTicketNumber);
        feedbackDetails.setRating(feedback.getStar_rating() != null ? feedback.getStar_rating().getValue() : 0);
        feedbackDetails.setComment(feedback.getComment());
        feedbackDetails.setCreatedAt(feedback.getCreatedAt());
        feedbackDetails.setUpdatedAt(feedback.getUpdatedAt());

        CreateFeedbackResponseDTO responseDTO = new CreateFeedbackResponseDTO(feedbackDetails);

        return responseDTO;
    }
}
