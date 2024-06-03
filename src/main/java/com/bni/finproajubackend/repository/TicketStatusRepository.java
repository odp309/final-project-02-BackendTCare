package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketStatusRepository extends JpaRepository<TicketStatus, Long> {
    TicketStatus findByTicket(@Param("ticket") Optional<Tickets> ticket);
}