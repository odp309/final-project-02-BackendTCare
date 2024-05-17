package com.bni.finproajubackend.controller.middleware;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.service.TokenRevocationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenCleanupTask {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TokenRevocationListService tokenRevocationListService;

    @Autowired
    private JWTInterface jwtService;

    @Scheduled(fixedRate = 900000) // Run every hour
    public void cleanupExpiredTokens() {
        Map<Object, Object> revokedTokens = redisTemplate.opsForHash().entries("tokenRevoked");
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<Object, Object> entry : revokedTokens.entrySet()) {
            String token = (String) entry.getKey();
            long expirationTime = jwtService.extractExpiration(String.valueOf(entry.getValue())).getTime();

            if (expirationTime < currentTime) {
                tokenRevocationListService.removeToken(token);
                redisTemplate.opsForHash().delete("revokedTokens", token);
            }
        }
    }
}
