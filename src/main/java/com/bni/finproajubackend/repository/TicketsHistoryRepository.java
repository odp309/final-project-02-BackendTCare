package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketsHistoryRepository extends JpaRepository<Tickets, Long> {
    Tickets findTicketById(long id);

}
