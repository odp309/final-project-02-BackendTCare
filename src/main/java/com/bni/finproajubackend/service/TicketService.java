package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketResponseTimeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TicketService implements TicketInterface {

    @Autowired
    private TicketsRepository ticketsRepository;

    @Autowired
    private TicketsHistoryRepository ticketHistoryRepository;

    @Autowired
    private AdminRepository adminRepository;

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
                        status == TicketStatus.Dalam_Proses ? 3L :
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
}
