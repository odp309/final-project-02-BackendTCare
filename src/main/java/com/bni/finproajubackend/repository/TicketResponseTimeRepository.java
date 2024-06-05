package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketResponseTimeRepository extends JpaRepository<TicketResponseTime, Long> {
}
