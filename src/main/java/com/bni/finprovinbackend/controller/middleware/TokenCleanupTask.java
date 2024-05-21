package com.bni.finprovinbackend.controller.middleware;

import com.bni.finprovinbackend.model.TokenRevocation;
import com.bni.finprovinbackend.repository.TokenRevocationRepository;
import com.bni.finprovinbackend.service.TokenRevocationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TokenCleanupTask {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private TokenRevocationListService tokenRevocationListService;

    @Scheduled(fixedRate = 600000)
    public void cleanupExpiredTokens() {
        try {
            List<TokenRevocation> expiredTokens = tokenRevocationRepository.findAll()
                    .stream()
                    .filter(tokenRevocation -> tokenRevocation.getExpirationTime().before(new Date()))
                    .toList();

            for (TokenRevocation tokenRevocation : expiredTokens) {
                tokenRevocationListService.removeToken(tokenRevocation.getToken());
            }
            System.out.println("==============================");
            System.out.println("Cleanup Success " + new Date());
        } catch (Exception e) {
            System.out.println("==============================");
            System.out.println("Cleanup Failed " + new Date());
            System.out.println(e.getMessage());
        }
    }
}
