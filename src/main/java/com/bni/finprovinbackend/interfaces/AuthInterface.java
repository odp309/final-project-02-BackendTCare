package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.login.LoginRequestDTO;
import com.bni.finprovinbackend.dto.login.LoginResponseDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenResponseDTO;
import org.apache.coyote.BadRequestException;

public interface AuthInterface {
    LoginResponseDTO login(LoginRequestDTO request);
    RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request);
    void logout(String token) throws BadRequestException;

//    RegisterResponseDTO register(RegisterRequestDTO request);
}
