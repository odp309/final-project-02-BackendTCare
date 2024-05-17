package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.TokenRevocation;

import java.util.Optional;

public interface TokenRevocationRepository {
    Optional<TokenRevocation> findByToken(String token);
}
