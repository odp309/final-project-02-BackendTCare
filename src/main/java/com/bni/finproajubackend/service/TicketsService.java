package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketsRequestDTO;
import com.bni.finproajubackend.interfaces.TicketsInterface;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketsService implements TicketsInterface {

    public Tickets createTicket(TicketsRequestDTO requestDTO, Authentication authentication) {
        Tickets ticket = new Tickets();

        try {
            TicketsRepository.save(ticket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Tickets getTicketById(Long id) {
        return ticketsRepository.findById(id).orElse(null);
    }
    public Tickets updateTicket(Long id, TicketsRequestDTO requestDTO) {
        Tickets ticket = ticketsRepository.findById(id).orElse(null);
        if (ticket != null) {
            try {
                ticketsRepository.save(ticket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<Tickets> getTickets(Object tickets) {
        return List.of();
    }
}
