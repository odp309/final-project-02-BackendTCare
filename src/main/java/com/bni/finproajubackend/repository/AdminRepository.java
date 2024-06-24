package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.user.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Override
    Optional<Admin> findById(Long aLong);
    Optional<Admin> findByNpp(String npp);
    List<Admin> findAllByDivisionTarget(DivisionTarget divisionTarget);
    @Query("SELECT a FROM Admin a LEFT JOIN TicketFeedback tf ON tf.ticket.admin = a WHERE a.divisionTarget = :divisionTarget GROUP BY a ORDER BY AVG(tf.star_rating) DESC")
    List<Admin> findAllByDivisionTargetOrderByAverageRatingDesc(DivisionTarget divisionTarget);
    @Query("SELECT a FROM Admin a WHERE a.user.username = :username")
    Admin findByUsername(String username);
}
