package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.login.LoginRequestDTO;
import com.bni.finprovinbackend.dto.login.LoginResponseDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finprovinbackend.dto.refreshToken.RefreshTokenResponseDTO;

public interface AuthInterface {
    LoginResponseDTO login(LoginRequestDTO request);
    RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request);


//    RegisterResponseDTO register(RegisterRequestDTO request);
}
