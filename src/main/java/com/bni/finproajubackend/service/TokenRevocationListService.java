package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.interfaces.TokenRevocationListInterface;
import com.bni.finproajubackend.model.TokenRevocation;
import com.bni.finproajubackend.repository.TokenRevocationRepository;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Getter
@Service
public class TokenRevocationListService implements TokenRevocationListInterface {

    @Autowired
    private TokenRevocationRepository tokenRevocationRepository;

    @Autowired
    private JWTInterface jwtService;
    private static final Logger logger = LoggerFactory.getLogger(TokenRevocationListService.class);
    private static final Marker TRL_MARKER = MarkerFactory.getMarker("TRL");

    @Override
    public void addToRevocationList(String token) throws BadRequestException {
        if (jwtService.extractExpiration(token).before(new Date()))
            throw new BadRequestException("Token Expired");

        TokenRevocation tokenRevocation = TokenRevocation.builder()
                .token(token)
                .expirationTime(jwtService.extractExpiration(token))
                .build();

        tokenRevocationRepository.save(tokenRevocation);
        logger.info(TRL_MARKER, "Token Revoked");
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return tokenRevocationRepository.findByToken(token).isPresent();
    }

    @Override
    public void removeToken(String token) {
        tokenRevocationRepository.deleteByToken(token);
    }
}
