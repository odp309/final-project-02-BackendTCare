package com.bni.finprovinbackend.controller;

import com.bni.finprovinbackend.dto.login.LoginRequestDTO;
import com.bni.finprovinbackend.dto.login.LoginResponseDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenResponseDTO;
import com.bni.finprovinbackend.interfaces.*;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {

    @Autowired
    private AuthInterface authService;
    @Autowired
    private RefreshTokenInterface refreshTokenService;
    @Autowired
    private JWTInterface jwtUtil;
    @Autowired
    private TemplateResInterface responseService;
    @Autowired
    private TokenRevocationListInterface tokenRevocationListService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO result = authService.login(request);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Login Success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(null, "Bad Credentials"));
        }
    }

    @PostMapping(value = "/refreshToken", produces = "application/json")
    public ResponseEntity resfreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenReq) {
        try {
            RefreshTokenResponseDTO result = authService.refreshToken(refreshTokenReq);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Refresh Token Acquired"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(null, e.getMessage()));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity logout(@RequestHeader(name = "Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer "))
                tokenRevocationListService.addToRevocationList(token.substring(7));
            return ResponseEntity.ok(responseService.apiSuccess(null, "Logout Successful"));
        } catch (RuntimeException | BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(null, e.getMessage()));
        }
    }
}
