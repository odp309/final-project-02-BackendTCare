package com.bni.finprovinbackend.service;

import com.bni.finprovinbackend.dto.login.LoginRequestDTO;
import com.bni.finprovinbackend.dto.login.LoginResponseDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenResponseDTO;
import com.bni.finprovinbackend.interfaces.AuthInterface;
import com.bni.finprovinbackend.interfaces.JWTInterface;
import com.bni.finprovinbackend.model.RefreshToken;
import com.bni.finprovinbackend.model.user.User;
import com.bni.finprovinbackend.repository.RoleRepository;
import com.bni.finprovinbackend.repository.UserRepository;
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
                .message("Login Success")
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
