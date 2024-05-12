package com.bni.finalproject01webservice.service;

import com.bni.finalproject01webservice.interfaces.JWTInterface;
import com.bni.finalproject01webservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTService implements JWTInterface {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(24 * 60 * 60);

        return Jwts
                .builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("role", user.getRole())
                .expiration(Date.from(expirationTime))
                .signWith(getJWTKey())
                .compact();
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getJWTKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public SecretKey getJWTKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    @Override
    public boolean isTokenValid(String token) {
        return extractExpiration(token).before(new Date());
    }
}
