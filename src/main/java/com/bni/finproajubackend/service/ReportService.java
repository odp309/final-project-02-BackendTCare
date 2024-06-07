package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.interfaces.ReportInterface;
import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketsRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService implements ReportInterface {

    @Autowired
    private TicketsRepository ticketsRepository;

    private Specification<Tickets> getSpecification(String category, String status, Integer rate, long year) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by category
            if (category != null) {
                TicketCategories ticketCategoryEnum = switch (category) {
                    case "\"Gagal Transfer\"" -> TicketCategories.Transfer;
                    case "\"Gagal TopUp\"" -> TicketCategories.TopUp;
                    case "\"Gagal Payment\"" -> TicketCategories.Payment;
                    default -> null;
                };
                if (ticketCategoryEnum != null) {
                    predicates.add(builder.equal(root.get("ticketCategory"), ticketCategoryEnum));
                }
            }

            // Filter by status
            if (status != null) {
                TicketStatus ticketStatus = switch (status) {
                    case "\"Diajukan\"" -> TicketStatus.Diajukan;
                    case "\"Diproses\"" -> TicketStatus.DalamProses;
                    case "\"Selesai\"" -> TicketStatus.Selesai;
                    default -> null;
                };
                if (ticketStatus != null) {
                    predicates.add(builder.equal(root.get("ticketStatus"), ticketStatus));
                }
            }

            // Filter by rate
            if (rate != null) {
                StarRating starRating = switch (rate) {
                    case 1 -> StarRating.Satu;
                    case 2 -> StarRating.Dua;
                    case 3 -> StarRating.Tiga;
                    case 4 -> StarRating.Empat;
                    case 5 -> StarRating.Lima;
                    default -> null;
                };
                if (starRating != null) {
                    predicates.add(builder.equal(root.get("starRating"), starRating));
                }
            }

            // Filter by year using date range
            LocalDateTime startOfYear = LocalDateTime.of((int) year, 1, 1, 0, 0);
            LocalDateTime endOfYear = LocalDateTime.of((int) year, 12, 31, 23, 59, 59);
            predicates.add(builder.between(root.get("createdAt"), startOfYear, endOfYear));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ReportResponseDTO buildReportResponse(List<Tickets> tickets, long year) {
        Map<Month, Long> monthlyCounts = tickets.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.getCreatedAt().getMonth(), Collectors.counting()));

        return ReportResponseDTO.builder()
                .january(monthlyCounts.getOrDefault(Month.JANUARY, 0L))
                .february(monthlyCounts.getOrDefault(Month.FEBRUARY, 0L))
                .march(monthlyCounts.getOrDefault(Month.MARCH, 0L))
                .april(monthlyCounts.getOrDefault(Month.APRIL, 0L))
                .may(monthlyCounts.getOrDefault(Month.MAY, 0L))
                .june(monthlyCounts.getOrDefault(Month.JUNE, 0L))
                .july(monthlyCounts.getOrDefault(Month.JULY, 0L))
                .august(monthlyCounts.getOrDefault(Month.AUGUST, 0L))
                .september(monthlyCounts.getOrDefault(Month.SEPTEMBER, 0L))
                .october(monthlyCounts.getOrDefault(Month.OCTOBER, 0L))
                .november(monthlyCounts.getOrDefault(Month.NOVEMBER, 0L))
                .december(monthlyCounts.getOrDefault(Month.DECEMBER, 0L))
                .total(monthlyCounts.values().stream().mapToLong(Long::longValue).sum())
                .totalAllReports(ticketsRepository.count())
                .year(year)
                .build();
    }

    @Override
    public ReportResponseDTO getCategory(String category, long year) {
        Specification<Tickets> spec = getSpecification(category, null, null, year);
        List<Tickets> tickets = ticketsRepository.findAll(spec);
        return buildReportResponse(tickets, year);
    }

    @Override
    public ReportResponseDTO getStatus(String status, long year) {
        Specification<Tickets> spec = getSpecification(null, status, null, year);
        List<Tickets> tickets = ticketsRepository.findAll(spec);
        return buildReportResponse(tickets, year);
    }

    @Override
    public ReportResponseDTO getRating(Integer rate, long year) {
        Specification<Tickets> spec = getSpecification(null, null, rate, year);
        List<Tickets> tickets = ticketsRepository.findAll(spec);
        return buildReportResponse(tickets, year);
    }
}
