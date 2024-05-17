package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.login.LoginRequestDTO;
import com.bni.finproajubackend.dto.login.LoginResponseDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenResponseDTO;
import com.bni.finproajubackend.interfaces.AuthInterface;
import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.model.RefreshToken;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.RoleRepository;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JWTInterface jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JWTInterface jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String username = authentication.getName();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        User user = userRepository.findByUsername(username);

        return LoginResponseDTO.builder()
                .accessToken(jwtService.generateToken(user))
                .token(refreshToken.getToken())
                .build();
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        return RefreshTokenResponseDTO.builder()
                .message("Access Token Granted")
                .accessToken(jwtService.generateToken(user))
                .build();
    }


}
