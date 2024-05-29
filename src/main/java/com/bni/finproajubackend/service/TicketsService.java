package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.TicketsInterface;
import com.bni.finproajubackend.model.ticket.TicketCategories;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.UUID;

public class TicketsService implements TicketsInterface {

    @Column
    public String ticketNumber(User user, TicketCategories categories) {
        LocalDate currentDate = LocalDate.now();
        String uuid = UUID.randomUUID().toString();
        String userId = user.getId().toString();
        String categoryId = categories.getId().toString();

        this.ticketNumber = currentDate.toString() + "-" + userId + "-" + categoryId + "-" + uuid;
        return ticketNumber;
    }

    private Tickets getTicketByUUID() {
        return null;
    }

    private void updateTicket (Tickets tickets) {
        updateTicket(tickets);
    }

    private void deleteTicket (Tickets tickets) {
        deleteTicket(tickets);
    }
}
