package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class TokenRevocationListService {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private JWTInterface jwtService;

    public void addToRevocationList(String token) {
        TokenRevocation tokenRevocation = TokenRevocation.builder()
                .token(token)
                .expirationTime(jwtService.extractExpiration(token))
                .build();
        tokenRevocationRepository.save(tokenRevocation);
    }

    public boolean isTokenRevoked(String token) {
        return tokenRevocationRepository.isTokenRevoked(token);
    }

    public void removeToken(String token) {
        tokenRevocationRepository.deleteByToken(token);
    }
}
