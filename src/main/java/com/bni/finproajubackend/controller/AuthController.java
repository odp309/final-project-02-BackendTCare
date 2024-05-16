package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.login.LoginRequestDTO;
import com.bni.finproajubackend.dto.login.LoginResponseDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenResponseDTO;
import com.bni.finproajubackend.interfaces.*;
import com.bni.finproajubackend.service.TokenRevocationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

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
    private TokenRevocationListService tokenRevocationListService;

//    @PostMapping("/init")
//    public ResponseEntity<InitResponseDTO> initRoleAndUser() {
//        InitResponseDTO result = userService.initRoleAndUser();
//        return ResponseEntity.ok(result);
//    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO result = authService.login(request);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiFailed(e));
        }
    }

    @PostMapping(value = "/refreshToken", produces = "application/json")
    public ResponseEntity resfreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenReq) {
        try {
            RefreshTokenResponseDTO result = authService.refreshToken(refreshTokenReq);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(e));
        }
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity logout(@RequestHeader(name = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer "))
            tokenRevocationListService.addToRevocationList(token.substring(7));
        String result = "Logged Out Successfully";
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }
}
