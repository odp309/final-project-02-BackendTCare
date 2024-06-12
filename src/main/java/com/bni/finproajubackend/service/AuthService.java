package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.login.LoginRequestDTO;
import com.bni.finproajubackend.dto.login.LoginResponseDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenResponseDTO;
import com.bni.finproajubackend.interfaces.AuthInterface;
import com.bni.finproajubackend.interfaces.JWTInterface;
import com.bni.finproajubackend.interfaces.RefreshTokenInterface;
import com.bni.finproajubackend.interfaces.TokenRevocationListInterface;
import com.bni.finproajubackend.model.RefreshToken;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.RoleRepository;
import com.bni.finproajubackend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RefreshTokenInterface refreshTokenService;
    private TokenRevocationListInterface tokenRevocationListService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");
    @Autowired
    private LoggerService loggerService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, JWTInterface jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, TokenRevocationListInterface tokenRevocationListService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.tokenRevocationListService = tokenRevocationListService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        logger.debug(SECURITY_MARKER, "Login request received for username: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

            logger.info(SECURITY_MARKER, "IP {} User {} successfully logged in", loggerService.getClientIp(), username);

            return LoginResponseDTO.builder()
                    .accessToken(jwtService.generateToken(user))
                    .token(refreshToken.getToken())
                    .build();
        } catch (Exception e) {
            logger.error(SECURITY_MARKER, "Error during login for username: {}", request.getUsername(), e);
            throw new RuntimeException("Login failed");
        }
    }

    @Override
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        logger.debug(SECURITY_MARKER, "Refresh token request received for token: {}", request.getToken());
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getToken())
                    .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));

            refreshTokenService.verifyExpiration(refreshToken);
            User user = refreshToken.getUser();

            logger.info(SECURITY_MARKER, "Refresh token successfully processed for user: {}", user.getUsername());

            return RefreshTokenResponseDTO.builder()
                    .message("Access Token Granted")
                    .accessToken(jwtService.generateToken(user))
                    .build();
        } catch (Exception e) {
            logger.error(SECURITY_MARKER, "Error during refresh token for token: {}", request.getToken(), e);
            throw new RuntimeException("Refresh token failed");
        }
    }

    @Override
    public void logout(String token) throws BadRequestException {
        logger.debug(SECURITY_MARKER, "Logout request received for token: {}", token);
        try {
            String actualToken = token.substring(7); // Assuming the token has "Bearer " prefix
            tokenRevocationListService.addToRevocationList(actualToken);
            refreshTokenService.removeRefreshToken(actualToken);

            logger.info(SECURITY_MARKER, "User successfully logged out");
        } catch (Exception e) {
            logger.error(SECURITY_MARKER, "Error during logout for token: {}", token, e);
            throw new BadRequestException("Logout failed");
        }
    }
}
