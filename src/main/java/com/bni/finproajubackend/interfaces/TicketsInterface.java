package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.model.ticket.Tickets;

import java.util.List;

public interface TicketsInterface {
    List<Tickets> getTickets(Object tickets);

}
