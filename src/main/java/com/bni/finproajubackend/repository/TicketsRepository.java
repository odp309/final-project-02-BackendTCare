package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    TicketStatus findStatusById(Status status);

    TicketStatus findByStatusName(Status newStatus);
}
