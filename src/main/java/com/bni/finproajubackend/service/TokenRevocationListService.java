package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.interfaces.TokenRevocationListInterface;
import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class TokenRevocationListService implements TokenRevocationListInterface {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private JWTInterface jwtService;

    @Override
    public void addToRevocationList(String token) throws BadRequestException {
        if (jwtService.extractExpiration(token).before(new Date()))
            throw new BadRequestException("Token Expired");

        TokenRevocation tokenRevocation = TokenRevocation.builder()
                .token(token)
                .expirationTime(jwtService.extractExpiration(token))
                .build();
        tokenRevocationRepository.save(tokenRevocation);
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return tokenRevocationRepository.findByToken(token).isPresent();
    }

    @Override
    public void removeToken(String token) {
        tokenRevocationRepository.deleteByToken(token);
    }
}
