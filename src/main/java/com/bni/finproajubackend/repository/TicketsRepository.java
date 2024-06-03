package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    Optional<Tickets> findById(Long id);

}
