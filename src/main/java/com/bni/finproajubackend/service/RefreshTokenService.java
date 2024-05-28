package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.RefreshTokenInterface;
import com.bni.finproajubackend.model.RefreshToken;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.RefreshTokenRepository;
import com.bni.finproajubackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class RefreshTokenService implements RefreshTokenInterface {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username);

        Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUserId(user.getId());

        if (existingTokenOptional.isEmpty()) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findByUsername(username))
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(86400)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                    .build();
            return refreshTokenRepository.save(refreshToken);
        }

        RefreshToken existingToken = existingTokenOptional.get();

//        if (existingToken.getExpiryDate().compareTo(Instant.now()) > 0) return existingToken;

        existingToken.setToken(UUID.randomUUID().toString());
        existingToken.setExpiryDate(Instant.now().plusSeconds(86400));
        return refreshTokenRepository.save(existingToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
}
