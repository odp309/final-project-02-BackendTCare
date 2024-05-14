package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.PersonRepository;
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
    private PersonRepository personRepository;

    public JWTService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(60 * 15);
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("id", user.getId())
                .claim("firstName", user.getPerson().getFirstName())
                .claim("lastName", user.getPerson().getLastName())
                .claim("role", user.getPerson().getAdmin() == null ? "nasabah" : user.getPerson().getAdmin().getRole().getRoleName())
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
        return extractExpiration(token).before(new Date());
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
