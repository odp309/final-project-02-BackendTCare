package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.TicketFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketFeedbackRepository extends JpaRepository<TicketFeedback, Long> {
}
