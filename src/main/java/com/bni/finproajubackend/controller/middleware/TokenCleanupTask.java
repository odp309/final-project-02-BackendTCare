package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.service.TokenRevocationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenCleanupTask {
    @Autowired
    private TokenRevocationListService tokenRevocationListService;

    public TokenCleanupTask(TokenRevocationListService tokenRevocationListService) {
        this.tokenRevocationListService = tokenRevocationListService;
    }

    @Scheduled(fixedRate = 60 * 10) // Run every hour
    public void cleanupExpiredTokens() {
        Map<String, Long> revokedTokens = (Map<String, Long>) tokenRevocationListService.getRevokedTokens();
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, Long> entry : revokedTokens.entrySet()) {
            String token = entry.getKey();
            long expirationTime = entry.getValue();

            if (expirationTime < currentTime) {
                tokenRevocationListService.removeToken(token);
            }
        }
    }
}
