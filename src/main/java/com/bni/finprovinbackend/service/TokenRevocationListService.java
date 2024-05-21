package com.bni.finprovinbackend.service;

import com.bni.finprovinbackend.interfaces.JWTInterface;
import com.bni.finprovinbackend.interfaces.TokenRevocationListInterface;
import com.bni.finprovinbackend.model.TokenRevocation;
import com.bni.finprovinbackend.repository.TokenRevocationRepository;
import lombok.Getter;
import org.apache.coyote.BadRequestException;
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

    @Override
    public void addToRevocationList(String token) throws BadRequestException {
        if (jwtService.extractExpiration(token).before(new Date()))
            throw new BadRequestException("Token Expired");

        TokenRevocation tokenRevocation = TokenRevocation.builder()
                .token(token)
                .expirationTime(jwtService.extractExpiration(token))
                .build();
        tokenRevocationRepository.save(tokenRevocation);
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
