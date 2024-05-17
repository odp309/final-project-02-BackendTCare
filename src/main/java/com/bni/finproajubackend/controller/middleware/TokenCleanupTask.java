package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import com.bni.finproajubackend.service.TokenRevocationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenCleanupTask {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private TokenRevocationListService tokenRevocationListService;

    @Scheduled(fixedRate = 900000)
    public void cleanupExpiredTokens() {
        List<TokenRevocation> expiredTokens = tokenRevocationRepository.findAll()
                .stream()
                .filter(tokenRevocation -> tokenRevocation.getExpirationTime().before(new Date()))
                .toList();

        for (TokenRevocation tokenRevocation : expiredTokens) {
            tokenRevocationListService.removeToken(tokenRevocation.getToken());
        }
        System.out.println("Cleanup Success");
    }
}
