package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.TicketCategories;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketStatusRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketInterface {

    @Autowired
    private TicketsRepository ticketsRepository;

    @Autowired
    private TicketStatusRepository ticketStatusRepository;

    public TicketService(TicketsRepository ticketsRepository) {
        this.ticketsRepository = ticketsRepository;
    }

    @Override
    public Tickets createTicket(TicketRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getTransaction() == null || requestDTO.getTicketCategory() == null || requestDTO.getDescription() == null) {
            return null;
        }

        Tickets newTicket = new Tickets();
        newTicket.setTransaction(newTicket.getTransaction());
        newTicket.setTicketStatus(newTicket.getTicketStatus());
        newTicket.setTicketCategory(newTicket.getTicketCategory());
        newTicket.setDescription(requestDTO.getDescription());

        newTicket.setTicketNumber(requestDTO.getTransaction().toString());

        TicketStatus initialStatus = new TicketStatus();
        initialStatus.setStatus(Status.Diajukan);
        ticketStatusRepository.save(initialStatus);
        newTicket.setTicketStatus(initialStatus);

        return ticketsRepository.save(newTicket);
    }

    @Override
    public Tickets updateTicketStatus(Long Id, Status status) {
        Optional<Tickets> optionalTicket = ticketsRepository.findById(Id);

        if (!optionalTicket.isPresent()) {
            return new Tickets();
        }

        Tickets ticket = optionalTicket.get();

        TicketStatus updatedStatus = ticketStatusRepository.findByTicket(optionalTicket);

        if (updatedStatus == null) {
            return new Tickets();
        }

        updatedStatus.setStatus(Status.DalamProses);
        ticketStatusRepository.save(updatedStatus);

        ticket.setTicketStatus(updatedStatus);
        ticketsRepository.save(ticket);

        return ticket;
    }

    @Override
    public Tickets getTicketDetails(Long Id) {
        Optional<Tickets> optionalTicket = ticketsRepository.findById(Id);

        return optionalTicket.orElse(null);
    }

    @Override
    public List<Tickets> getAllTickets() {
        return List.of();
    }
}
