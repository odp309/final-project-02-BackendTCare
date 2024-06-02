package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
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

    public TicketService(TicketsRepository ticketsRepository) {
        this.ticketsRepository = ticketsRepository;
    }

    @Override
    public Tickets createTicket(TicketRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getTransaction() == null || requestDTO.getTicketCategory() == null || requestDTO.getDescription() == null) {
            return null;
        }

        Tickets newTicket = new Tickets();
        newTicket.setTransactionId(requestDTO.getTransaction());
        newTicket.setTicketCategoryId(requestDTO.getTicketCategory());
        newTicket.setDescription(requestDTO.getDescription());

        newTicket.setTicketNumber(requestDTO.getTransaction().toString());

        TicketStatus initialStatus = ticketsRepository.findStatusById(Status.Diajukan);
        newTicket.setTicketStatus(initialStatus.getStatus());

        return ticketsRepository.save(newTicket);
    }

    @Override
    public Tickets updateTicketStatus(Long Id, Status newStatus) {
        Optional<Tickets> optionalTicket = ticketsRepository.findById(Id);

        if (optionalTicket.isPresent()) {
            Tickets ticket = optionalTicket.get();

            TicketStatus updatedStatus = ticketsRepository.findByStatusName(newStatus);

            if (updatedStatus != null) {
                ticket.setTicketStatus(updatedStatus.getStatus());
                return ticketsRepository.save(ticket);
            } else {
                return null;
            }
        } else {
            return null;
        }
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
