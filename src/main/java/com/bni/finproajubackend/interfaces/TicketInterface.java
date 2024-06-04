package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface TicketInterface {
    Tickets createTicket(TicketRequestDTO requestDTO);
    Tickets getTicketDetails(Long ticketId);
    Tickets updateTicketStatus(Long id, TicketStatus status);

    List<TicketsHistoryRepository> getTicketsHistory(long id);

    List<TicketHistoryResponseDTO> getTicketHistory(Long ticketId);

    List<Tickets> getAllTickets();

}
