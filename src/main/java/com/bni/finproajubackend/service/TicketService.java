package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketStatusRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketInterface {

    @Autowired
    private TicketsRepository ticketsRepository;
    private TicketStatusRepository ticketStatusRepository;

    public TicketService(TicketsRepository ticketsRepository, TicketStatusRepository ticketStatusRepository) {
        this.ticketsRepository = ticketsRepository;
        this.ticketStatusRepository = ticketStatusRepository;
    }

    @Override
    public Tickets createTicket(TicketRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getTransactionId() == null || requestDTO.getTicketCategoryId() == null || requestDTO.getDescription() == null) {
            throw new IllegalArgumentException("Invalid ticket creation request");
        }

        Tickets newTicket = new Tickets();
        newTicket.setTicketNumber(UUID.randomUUID().toString());
        newTicket.setTransactionId(requestDTO.getTransactionId());
        newTicket.setTicketCategoryId(requestDTO.getTicketCategoryId());
        newTicket.setDescription(requestDTO.getDescription());

        TicketStatus initialStatus = ticketStatusRepository.findStatusById(Status.Diajukan);
        newTicket.setTicketStatus(initialStatus.getStatus());

        return ticketsRepository.save(newTicket);
    }

    @Override
    public Tickets updateTicketStatus(Long ticketId, Status newStatus) {
        Tickets tickets = ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        TicketStatus updatedStatus = ticketStatusRepository.findByStatusName(newStatus);

        if (updatedStatus == null) {
            throw new IllegalArgumentException("Invalid ticket status: " + newStatus);
        }

        tickets.setTicketStatus(updatedStatus.getStatus());

        return ticketsRepository.save(tickets);
    }

    @Override
    public Tickets getTicketDetails(Long ticketId) {
        return ticketsRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));
    }

    @Override
    public List<Tickets> getAllTickets() {
        return List.of();
    }
}
