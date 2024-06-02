package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.Tickets;

import java.util.List;


public interface TicketInterface {
    Tickets createTicket(TicketRequestDTO requestDTO);
    Tickets updateTicketStatus(Long Id, Status newStatus);
    Tickets getTicketDetails(Long ticketId);
    List<Tickets> getAllTickets();

}
