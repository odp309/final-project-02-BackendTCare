package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketsHistoryRepository extends JpaRepository<TicketHistory, Long> {
    TicketHistory findTicketHistoryById(long id);
}
