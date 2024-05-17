package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.TokenRevocation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRevocationRepository extends JpaRepository<TokenRevocation, Long> {
    Optional<TokenRevocation> findByToken(String token);
    @Transactional
    boolean deleteByToken(String token);
}
