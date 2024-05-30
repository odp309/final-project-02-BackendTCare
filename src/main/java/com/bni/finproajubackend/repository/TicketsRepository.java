package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long> {
    static Optional<Object> findbyId(Long id) {
        return Optional.empty();
    }

    Tickets findTicketsById(long id);

    Tickets findTicketsById(String id);
}
