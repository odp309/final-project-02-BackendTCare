package com.bni.finproajubackend.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class TokenRevocationListService {
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    public void addToRevocationList(String token) {
        revokedTokens.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    public void removeToken(String token) {
        revokedTokens.remove(token);
    }
}
