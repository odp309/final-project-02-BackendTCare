package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketFeedbackRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import com.bni.finproajubackend.service.TokenRevocationListService;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.sql.ast.tree.expression.Star;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TicketClosedTask {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private TokenRevocationListService tokenRevocationListService;

    private static final Logger logger = LoggerFactory.getLogger(TicketClosedTask.class);
    private static final Marker TICKET_MARKER = MarkerFactory.getMarker("TICKET");
    private TicketsRepository ticketsRepository;
    @Autowired
    private TicketFeedbackRepository ticketFeedbackRepository;

    //    @Scheduled(fixedRate = 259200000)
    @Scheduled(fixedRate = 600000)
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

            List<TicketFeedback> expiredFeedback = ticketFeedbackRepository.findAll()
                    .stream()
                    .filter(feedback -> feedback.getStar_rating() == null)
                    .filter(feedback -> feedback.getCreatedAt().isBefore(tenMinutesAgo))
                    .toList();

            for (TicketFeedback exFeed : expiredFeedback) {
                exFeed.setStar_rating(StarRating.Empat);
                exFeed.setComment("Kinerja Baik");
                ticketFeedbackRepository.save(exFeed);
            }
            logger.info(TICKET_MARKER, "Closing Ticket Feedback successfully");
        } catch (Exception e) {
            logger.warn(TICKET_MARKER, "Closing Ticket Feedback failed: {}", e.getMessage());
        }
    }


}
