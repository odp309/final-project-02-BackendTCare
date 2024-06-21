package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketsHistoryRepository extends JpaRepository<TicketHistory, Long> {
    TicketHistory findTicketById(long id);

    List<TicketHistory> findAllByTicket(Tickets ticket);

    TicketHistory findFirstByTicketOrderByCreatedAtDesc(Tickets ticket);
}
