package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.security.core.Authentication;

public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, TicketStatus status, Authentication authentication);
}
