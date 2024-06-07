package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TrackTicketStatusResponseDTO;
import com.bni.finproajubackend.interfaces.TrackTicketStatusInterface;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketHistoryStatusService implements TrackTicketStatusInterface {
    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private TicketsHistoryRepository ticketsHistoryRepository;

    @Override
    public List<TrackTicketStatusResponseDTO> trackTicketStatus(Long id) {
        Tickets ticket = ticketsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        List<TicketHistory> ticketHistories = ticketsHistoryRepository.findAllByTicket(ticket);

        return ticketHistories.stream().map(ticketHistory -> {
            TrackTicketStatusResponseDTO response = new TrackTicketStatusResponseDTO();
            response.setPic(ticketHistory.getAdmin().getFirstName() + " " + ticketHistory.getAdmin().getLastName());
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketHistory.getDate());
            response.setDate(formattedDate);
            response.setDescription(ticketHistory.getDescription());
            return response;
        }).collect(Collectors.toList());
    }
}
