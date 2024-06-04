package com.bni.finproajubackend.service;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bni.finproajubackend.util.EmailService;

@Service
public class TicketService {

    @Autowired
    private TicketsRepository ticketsRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Tickets updateTicketStatus(Long ticketId, TicketStatus status) {
        Tickets ticket = ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setTicketStatus(status);
        ticketsRepository.save(ticket);

        // Send email notification
        emailService.sendNotification(ticket);

        //return "Ticket status with id " + ticket.getId() + " successfully updated";
        return ticket;
    }
}
