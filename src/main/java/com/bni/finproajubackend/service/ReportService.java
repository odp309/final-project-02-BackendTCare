package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.interfaces.ReportInterface;
import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

            Optional.ofNullable(path).map(p -> p.equals("category")).ifPresent(p -> {
                TicketCategories ticketCategoryEnum = switch (queryParams.get("name").toLowerCase()) {
                    case "gagal transfer" -> TicketCategories.Transfer;
                    case "gagal topup" -> TicketCategories.TopUp;
                    case "gagal payment" -> TicketCategories.Payment;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
            });

            Optional.ofNullable(path).map(p -> p.equals("status")).ifPresent(p -> {
                TicketStatus ticketStatusEnum = switch (queryParams.get("name").toLowerCase()) {
                    case "diajukan" -> TicketStatus.Diajukan;
                    case "dalam proses" -> TicketStatus.DalamProses;
                    case "selesai" -> TicketStatus.Selesai;
                    default -> null;
                };
                predicates.add(builder.equal(root.get("ticketStatus"), ticketStatusEnum));
            });

            Optional.ofNullable(path).map(p -> p.equals("rating")).ifPresent(p -> {
                Join<Tickets, TicketFeedback> feedbackJoin = root.join("ticketFeedbacks", JoinType.LEFT);
                if (queryParams.get("rate").equals("4")) {
                    predicates.add(
                            builder.or(
                                    builder.isNull(feedbackJoin.get("starRating")),
                                    builder.equal(feedbackJoin.get("starRating"), StarRating.Empat)
                            )
                    );
                } else {
                    predicates.add(builder.equal(feedbackJoin.get("starRating"), StarRating.fromValue(Integer.parseInt(queryParams.get("rate")))));
                }
            });

            Optional.ofNullable(path).map(p -> p.equals(("reopened"))).ifPresent(p -> predicates.add(builder.isNotEmpty(root.get("referenceNumber"))));

            Optional.ofNullable(path).map(p -> p.equals(("autoclose"))).ifPresent(p -> {
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                predicates.add(builder.equal(responseJoin.get("responseTime"), 0));
            });

            Optional.ofNullable(path).map(p -> p.equals(("sla-performance"))).ifPresent(p -> {
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                predicates.add(builder.lessThan(responseJoin.get("responseTime"), 15));
            });

            Optional.ofNullable(path).filter(p -> p.equals("sla-completed")).ifPresent(p -> {
                Join<Tickets, TicketResponseTime> responseJoin = root.join("ticketResponseTime", JoinType.LEFT);
                List<Expression<?>> groupBy = new ArrayList<>();
                groupBy.add(responseJoin.get("responseTime"));

                query.multiselect(
                        builder.selectCase()
                                .when(builder.between(responseJoin.get("responseTime"), 0, 5), "0-5_HK")
                                .when(builder.between(responseJoin.get("responseTime"), 6, 10), "6-10_HK")
                                .when(builder.between(responseJoin.get("responseTime"), 11, 15), "11-15_HK")
                                .when(builder.greaterThan(responseJoin.get("responseTime"), 15), ">15_HK")
                                .otherwise("unknown").alias("interval"),
                        builder.count(root).alias("count")
                ).groupBy(responseJoin.get("responseTime"));
                query.where(builder.and(predicates.toArray(new Predicate[0])));
            });

            Optional.ofNullable(path).filter(p -> p.equals("total")).ifPresent(p -> {
                // Here you can add more filters based on queryParams if needed
                // For now, it's assumed to just count all reports
            });

            Optional.ofNullable(path).filter(p -> p.equals("submitted")).ifPresent(p -> predicates.add(builder.equal(root.get("ticketStatus"), TicketStatus.Diajukan)));

            Optional.ofNullable(path).filter(p -> p.equals("processed")).ifPresent(p -> predicates.add(builder.equal(root.get("ticketStatus"), TicketStatus.DalamProses)));

            Optional.ofNullable(path).filter(p -> p.equals("completed")).ifPresent(p -> predicates.add(builder.equal(root.get("ticketStatus"), TicketStatus.Selesai)));

            // Filter by month and group by week
            String monthStr = queryParams.get("month");
            if (monthStr != null && (path.equals("category") || path.equals("status") || path.equals("rating"))) {
                Month month = Month.valueOf(monthStr.toUpperCase());
                LocalDate startOfMonth = LocalDate.of(2024, month, 1);
                LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

                // Calculate week ranges within the month
                List<LocalDate[]> weeks = calculateWeeksInMonth(startOfMonth, endOfMonth);

                // Add predicates for each week
                for (LocalDate[] week : weeks) {
                    LocalDateTime startOfWeek = week[0].atStartOfDay();
                    LocalDateTime endOfWeek = week[1].atTime(23, 59, 59);
                    predicates.add(builder.between(root.get("createdAt"), startOfWeek, endOfWeek));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<LocalDate[]> calculateWeeksInMonth(LocalDate startOfMonth, LocalDate endOfMonth) {
        List<LocalDate[]> weeks = new ArrayList<>();
        LocalDate startOfWeek = startOfMonth;
        while (startOfWeek.isBefore(endOfMonth) || startOfWeek.equals(endOfMonth)) {
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
        return null;
    }

//    private ReportResponseDTO buildReportResponse(List<Tickets> tickets, long year) {
//        Map<Month, Long> monthlyCounts = tickets.stream()
//                .collect(Collectors.groupingBy(ticket -> ticket.getCreatedAt().getMonth(), Collectors.counting()));
//
//        return ReportResponseDTO.builder()
//                .january(monthlyCounts.getOrDefault(Month.JANUARY, 0L))
//                .february(monthlyCounts.getOrDefault(Month.FEBRUARY, 0L))
//                .march(monthlyCounts.getOrDefault(Month.MARCH, 0L))
//                .april(monthlyCounts.getOrDefault(Month.APRIL, 0L))
//                .may(monthlyCounts.getOrDefault(Month.MAY, 0L))
//                .june(monthlyCounts.getOrDefault(Month.JUNE, 0L))
//                .july(monthlyCounts.getOrDefault(Month.JULY, 0L))
//                .august(monthlyCounts.getOrDefault(Month.AUGUST, 0L))
//                .september(monthlyCounts.getOrDefault(Month.SEPTEMBER, 0L))
//                .october(monthlyCounts.getOrDefault(Month.OCTOBER, 0L))
//                .november(monthlyCounts.getOrDefault(Month.NOVEMBER, 0L))
//                .december(monthlyCounts.getOrDefault(Month.DECEMBER, 0L))
//                .total(monthlyCounts.values().stream().mapToLong(Long::longValue).sum())
//                .totalAllReports(ticketsRepository.count())
//                .year(year)
//                .build();
//    }

}
