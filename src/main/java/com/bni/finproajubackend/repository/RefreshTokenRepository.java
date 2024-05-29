package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long id);
    @Transactional
    void deleteByToken(String token);
}
