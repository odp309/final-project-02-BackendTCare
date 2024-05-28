package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenInterface {
    public RefreshToken createRefreshToken(String username);

    public Optional<RefreshToken> findByToken(String token);

    public RefreshToken verifyExpiration(RefreshToken token);

    public void removeRefreshToken(String token);
}
