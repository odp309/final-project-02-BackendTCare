package com.bni.finproajubackend.service;

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
    private RedisTemplate<String, Object> redisTemplate;

    public void addToRevocationList(String token) {
        redisTemplate.opsForValue().set(token, "revoked");
    }

    public boolean isTokenRevoked(String token) {
        return redisTemplate.hasKey(token);
    }

    public void removeToken(String token) {
        redisTemplate.delete(token);
    }
}
