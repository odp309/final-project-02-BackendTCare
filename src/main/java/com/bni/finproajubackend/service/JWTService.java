package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTService implements JWTInterface {

    @Value("${my.secret.key}")
    private String mySecretKey;
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");
    @Autowired
    private LoggerService loggerService;

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(86400);
        logger.info(SECURITY_MARKER, "IP {}, Generating token", loggerService.getClientIp());
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("firstName", user.getAdmin() == null ? user.getNasabah().getFirst_name() : user.getAdmin().getFirstName() )
                .claim("lastName", user.getAdmin() == null ? user.getNasabah().getLast_name() : user.getAdmin().getLastName() )
                .claim("role", user.getAdmin() == null ? "nasabah" : user.getAdmin().getRole().getRoleName())
                .claim("division", user.getAdmin() == null ? null : user.getAdmin().getDivisionTarget().name())
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
