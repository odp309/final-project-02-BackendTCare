package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import com.bni.finproajubackend.service.TokenRevocationListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    private static final Marker TOKEN_MARKER = MarkerFactory.getMarker("TOKEN");

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
            logger.info(TOKEN_MARKER, "Cleanup expired tokens successfully");
        } catch (Exception e) {
            logger.warn(TOKEN_MARKER, "Cleanup expired tokens failed: {}", e.getMessage());
        }
    }
}
