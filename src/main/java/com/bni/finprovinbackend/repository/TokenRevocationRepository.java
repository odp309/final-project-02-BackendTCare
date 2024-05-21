package com.bni.finprovinbackend.repository;

import com.bni.finprovinbackend.model.TokenRevocation;
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
