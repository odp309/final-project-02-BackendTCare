package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.login.LoginRequestDTO;
import com.bni.finproajubackend.dto.login.LoginResponseDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenResponseDTO;
import org.apache.coyote.BadRequestException;

public interface AuthInterface {
    LoginResponseDTO login(LoginRequestDTO request);
    RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request);
    void logout(String token) throws BadRequestException;

//    RegisterResponseDTO register(RegisterRequestDTO request);
}
