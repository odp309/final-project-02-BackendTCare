package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;

public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, TicketStatus status);
}
