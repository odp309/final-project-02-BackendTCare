package com.bni.finproajubackend.interfaces;

import org.apache.coyote.BadRequestException;

public interface TokenRevocationListInterface {
    void addToRevocationList(String token) throws BadRequestException;

    boolean isTokenRevoked(String token);

    void removeToken(String token);
}
