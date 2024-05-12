package com.bni.finalproject01webservice.interfaces;

import com.bni.finalproject01webservice.model.User;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;

public interface JWTInterface {
    String generateToken(User user);

    Claims extractAllClaims(String token);

    SecretKey getJWTKey();

    Date extractExpiration(String token);

    boolean isTokenValid(String token);
}
