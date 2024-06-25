package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.interfaces.ReportInterface;
import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.TicketsRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements ReportInterface {

    @Autowired
    private TicketsRepository ticketsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final Marker REPORT_MARKER = MarkerFactory.getMarker("REPORT");

    @Autowired
    private LoggerService loggerService;

    private Specification<Tickets> getSpecification(Map<String, String> queryParams, String path) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if ("category".equals(path) && queryParams.get("name") != null) {
                logger.info("Category Triggered");
                TicketCategories ticketCategoryEnum = switch (queryParams.get("name").toLowerCase()) {
                    case "gagal transfer" -> TicketCategories.Transfer;
                    case "gagal topup" -> TicketCategories.TopUp;
                    case "gagal payment" -> TicketCategories.Payment;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
            }

            if ("status".equals(path) && queryParams.get("name") != null) {
                logger.info("Status Triggered");
                TicketStatus ticketStatusEnum = switch (queryParams.get("name").toLowerCase()) {
                    case "diajukan" -> TicketStatus.Diajukan;
                    case "dalam proses" -> TicketStatus.DalamProses;
                    case "selesai" -> TicketStatus.Selesai;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketStatus"), ticketStatusEnum));
            }

            if ("rating".equals(path) && queryParams.get("rate") != null) {
                logger.info("Rating Triggered");
                Join<Tickets, TicketFeedback> feedbackJoin = root.join("ticketFeedback", JoinType.LEFT);
                if ("4".equals(queryParams.get("rate"))) {
                    predicates.add(
                            builder.or(
                                    builder.isNull(feedbackJoin.get("star_rating")),
                                    builder.equal(feedbackJoin.get("star_rating"), StarRating.Empat)
                            )
                    );
                } else {
                    predicates.add(builder.equal(feedbackJoin.get("star_rating"), StarRating.fromValue(Integer.parseInt(queryParams.get("rate")))));
                }
            }

            if ("reopened".equals(path)) {
                logger.info("Reopened Triggered");
                predicates.add(builder.and(
                        builder.isNotNull(root.get("referenceNumber")),
                        builder.notEqual(root.get("referenceNumber"), "")
                ));
            }

            if ("autoclose".equals(path)) {
                logger.info("AutoClose Triggered");
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                predicates.add(builder.equal(responseJoin.get("responseTime"), 0));
            }

            if ("sla-performance".equals(path)) {
                logger.info("Sla Performance Triggered");
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                predicates.add(builder.lessThan(responseJoin.get("responseTime"), 15));
            }

            if ("sla-completed".equals(path)) {
                logger.info("Sla Completed Triggered");
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                query.multiselect(
                        builder.selectCase()
                                .when(builder.between(responseJoin.get("responseTime"), 0, 5), "0-5_HK")
                                .when(builder.between(responseJoin.get("responseTime"), 6, 10), "6-10_HK")
                                .when(builder.between(responseJoin.get("responseTime"), 11, 15), "11-15_HK")
                                .when(builder.greaterThan(responseJoin.get("responseTime"), 15), ">15_HK")
                                .otherwise("unknown").alias("interval"),
                        builder.count(root.get("id")).alias("count")
                ).groupBy(root.get("id"), responseJoin.get("responseTime"));
                query.where(builder.and(predicates.toArray(new Predicate[0])));
            }

            if ("total".equals(path) || "submitted".equals(path)) {
                // Add more filters if needed
                logger.info("Total Triggered");
            }

            if ("processed".equals(path)) {
                logger.info("Processed Triggered");
                predicates.add(builder.equal(root.get("ticketStatus"), TicketStatus.DalamProses));
            }

            if ("completed".equals(path)) {
                logger.info("Completed Triggered");
                predicates.add(builder.equal(root.get("ticketStatus"), TicketStatus.Selesai));
            }

            String filter = queryParams.get("filter");

            if ("all".equals(filter)) {
                // no additional filter
                logger.info("All Triggered");
            }

            if ("division".equals(filter)) {
                DivisionTarget divisionTarget = switch (queryParams.get("division").toLowerCase()) {
                    case "dgo" -> DivisionTarget.DGO;
                    case "cxc" -> DivisionTarget.CXC;
                    case "wpp" -> DivisionTarget.WPP;
                    case "system" -> DivisionTarget.SYSTEM;
                    default -> null;
                };
                logger.info("Division Triggered: {}", divisionTarget);
                if (divisionTarget != DivisionTarget.SYSTEM)
                    predicates.add(builder.equal(root.get("divisionTarget"), divisionTarget));
            }

            Optional.ofNullable(queryParams.get("adminid")).ifPresent(username -> {
                logger.info("Admin Triggered");
                Join<Tickets, Admin> adminJoin = root.join("admin", JoinType.LEFT);
                Join<Admin, User> userJoin = adminJoin.join("user", JoinType.LEFT);
                predicates.add(builder.equal(userJoin.get("username"), username));
            });

            if (queryParams.containsKey("month")) {
                logger.info("Month Triggered");
                Month month = Month.valueOf(queryParams.get("month").toUpperCase());
                LocalDate startOfMonth = LocalDate.of(2024, month, 1);
                LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
                predicates.add(builder.between(root.get("createdAt"), startOfMonth, endOfMonth));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<LocalDate[]> calculateWeeksInMonth(LocalDate startOfMonth, LocalDate endOfMonth) {
        List<LocalDate[]> weeks = new ArrayList<>();
        LocalDate startOfWeek = startOfMonth;
        while (!startOfWeek.isAfter(endOfMonth)) {
            LocalDate endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            if (endOfWeek.isAfter(endOfMonth)) {
                endOfWeek = endOfMonth;
            }
            weeks.add(new LocalDate[]{startOfWeek, endOfWeek});
            startOfWeek = endOfWeek.plusDays(1);
        }
        return weeks;
    }

    @Override
    public ReportResponseDTO getReports(Map<String, String> queryParams, String path) {
        try {
            logger.info("queryParams: {}", queryParams);
            logger.info("path: {}", path);
            List<Tickets> tickets = ticketsRepository.findAll(getSpecification(queryParams, path));
            logger.info("tickets: {}", tickets.stream().count());
            return switch (path) {
                case "category", "rating", "status" -> generateWeeklyReport(tickets);
                case "total", "submitted" -> getTotal(tickets);
                case "processed", "completed" -> getReportsData(tickets);
                case "reopened", "autoclose" -> getPercentageReport(tickets);
                case "sla-performance" -> getSlaPerformanceReport(tickets);
                case "sla-completed" -> getSlaCompletionReport(tickets);
                default -> null;
            };
        } catch (Exception e) {
            logger.warn("Error getting reports: {}", e.getMessage());
            throw new RuntimeException("Error");
        }
    }

    private ReportResponseDTO getSlaCompletionReport(List<Tickets> tickets) {
        Map<String, Long> slaCompleted = new HashMap<>();
        slaCompleted.put("0-5_HK", tickets.stream().filter(ticket -> {
            TicketResponseTime responseTime = ticket.getTicketResponseTime();
            return responseTime != null && responseTime.getResponseTime() >= 0 && responseTime.getResponseTime() <= 5;
        }).count());
        slaCompleted.put("6-10_HK", tickets.stream().filter(ticket -> {
            TicketResponseTime responseTime = ticket.getTicketResponseTime();
            return responseTime != null && responseTime.getResponseTime() >= 6 && responseTime.getResponseTime() <= 10;
        }).count());
        slaCompleted.put("11-15_HK", tickets.stream().filter(ticket -> {
            TicketResponseTime responseTime = ticket.getTicketResponseTime();
            return responseTime != null && responseTime.getResponseTime() >= 11 && responseTime.getResponseTime() <= 15;
        }).count());
        slaCompleted.put(">15_HK", tickets.stream().filter(ticket -> {
            TicketResponseTime responseTime = ticket.getTicketResponseTime();
            return responseTime != null && responseTime.getResponseTime() > 15;
        }).count());

        int total = slaCompleted.values().stream().mapToInt(Long::intValue).sum();

        Map<String, Object> sla_completed = new HashMap<>();
        sla_completed.put("sla_completed", slaCompleted);

        return ReportResponseDTO.builder()
                .result(sla_completed)
                .total(total)
                .total_reports((int) ticketsRepository.count())
                .year("2024")
                .build();
    }

    private ReportResponseDTO getSlaPerformanceReport(List<Tickets> tickets) {
        double percentage = (double) tickets.size() / ticketsRepository.count() * 100;
        if (Double.isNaN(percentage)) percentage = 0;
        String formattedPercentage = String.format("%.2f", percentage);
        Map<String, Object> result = Map.of("percentage_sla_performance", Double.parseDouble(formattedPercentage));
        return buildReportResponseDTO(tickets, result);
    }

    private ReportResponseDTO getPercentageReport(List<Tickets> tickets) {
        double percentage = (double) tickets.size() / ticketsRepository.count() * 100;
        if (Double.isNaN(percentage)) percentage = 0;
        String formattedPercentage = String.format("%.2f", percentage);
        Map<String, Object> result = Map.of("percentage", Double.parseDouble(formattedPercentage));
        return buildReportResponseDTO(tickets, result);
    }

    private ReportResponseDTO getReportsData(List<Tickets> tickets) {
        Map<String, Object> result = Map.of("reports", tickets.size());
        return buildReportResponseDTO(tickets, result);
    }

    private ReportResponseDTO getTotal(List<Tickets> tickets) {
        Map<String, Object> result = Map.of("total_reports", tickets.size());
        return buildReportResponseDTO(tickets, result);
    }

    private ReportResponseDTO generateWeeklyReport(List<Tickets> tickets) {
        logger.info("Generate Weekly Triggered");
        Map<String, Integer> weeklyCounts = new HashMap<>();
        weeklyCounts.put("minggu_1", 0);
        weeklyCounts.put("minggu_2", 0);
        weeklyCounts.put("minggu_3", 0);
        weeklyCounts.put("minggu_4", 0);

        for (Tickets ticket : tickets) {
            LocalDate createdAt = ticket.getCreatedAt().toLocalDate();
            int weekOfMonth = createdAt.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            String weekKey = "minggu_" + weekOfMonth;
            weeklyCounts.merge(weekKey, 1, Integer::sum);
        }

        Map<String, Object> result = new HashMap<>(weeklyCounts);

        return buildReportResponseDTO(tickets, result);
    }

    private ReportResponseDTO buildReportResponseDTO(List<Tickets> tickets, Map<String, Object> result) {
        logger.info("build response Triggered");
        return ReportResponseDTO.builder()
                .result(result)
                .total(tickets.size())
                .total_reports((int) ticketsRepository.count())
                .year("2024")
                .build();
    }
}

