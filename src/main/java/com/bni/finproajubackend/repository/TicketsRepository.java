package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    Tickets findTicketById(long id);
}
