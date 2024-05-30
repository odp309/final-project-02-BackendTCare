package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketStatusRepository extends JpaRepository<TicketStatus, Long> {
    TicketStatus findStatusById(Status status);

    TicketStatus findByStatusName(Status newStatus);
}
