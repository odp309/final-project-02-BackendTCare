package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    Optional<Tickets> findById(Long id);
    Tickets findByTicketNumber(String ticketNumber);
    Page<Tickets> findAll(Specification<Tickets> spec, Pageable pageable);
    List<Tickets> findAll(Specification<Tickets> spec, Sort by);
    List<Tickets> findAll(Specification<Tickets> spec);
}