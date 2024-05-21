package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.model.user.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.util.Date;

public interface JWTInterface {
    String generateToken(User user);

    Claims extractAllClaims(String token);

    SecretKey getJWTKey();

    Date extractExpiration(String token);

    boolean isTokenValid(String token);

    String resolveToken(HttpServletRequest request);
}
