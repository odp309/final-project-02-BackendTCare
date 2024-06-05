package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TicketsHistoryRepository extends JpaRepository<Tickets, Long> {
    Tickets findTicketById(long id);

}
