package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Long>, JpaSpecificationExecutor<Tickets> {
    Optional<Tickets> findById(Long id);

    Tickets findByTicketNumber(String ticketNumber);
    Tickets findByReferenceNumber(String ticketNumber);

    @Query("SELECT t FROM Tickets t ORDER BY " +
            "CASE t.ticketStatus " +
            "WHEN 'Diajukan' THEN 1 " +
            "WHEN 'DalamProses' THEN 2 " +
            "WHEN 'Selesai' THEN 3 " +
            "ELSE 4 END ASC")
    Page<Tickets> findAllWithCustomOrder(Specification<Tickets> spec, Pageable pageable);
    Page<Tickets> findAll(Specification<Tickets> spec, Pageable pageable);
    List<Tickets> findAll(Specification<Tickets> spec, Sort by);
    List<Tickets> findAll(Specification<Tickets> spec);

    List<Tickets> findByTransaction(Transaction transaction);
}