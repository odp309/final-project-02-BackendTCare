package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketsStatusRepository extends JpaRepository<TicketStatus, Long> {
    TicketsStatusRepository findStatusById(long id);
}
