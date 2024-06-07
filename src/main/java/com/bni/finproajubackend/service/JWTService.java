package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTService implements JWTInterface {

    @Value("${my.secret.key}")
    private String mySecretKey;

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(3600000);
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("id", user.getId())
                .claim("firstName", user.getAdmin() == null ? user.getNasabah().getFirstName() : user.getAdmin().getFirstName() )
                .claim("lastName", user.getAdmin() == null ? user.getNasabah().getLastName() : user.getAdmin().getLastName() )
                .claim("role", user.getAdmin() == null ? "nasabah" : user.getAdmin().getRole().getRoleName())
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
        byte[] keyBytes = Decoders.BASE64.decode(mySecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    @Override
    public boolean isTokenValid(String token) {
        return new Date().before(extractExpiration(token));
    }

    public String resolveToken(HttpServletRequest request) {
        String TOKEN_HEADER = "Authorization";
        String bearerToken = request.getHeader(TOKEN_HEADER);
        String TOKEN_PREFIX = "Bearer ";
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
