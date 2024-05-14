package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.login.LoginRequestDTO;
import com.bni.finproajubackend.dto.login.LoginResponseDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenRequestDTO;
import com.bni.finproajubackend.dto.refreshToken.RefreshTokenResponseDTO;

public interface AuthInterface {
    LoginResponseDTO login(LoginRequestDTO request);
    RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request);


//    RegisterResponseDTO register(RegisterRequestDTO request);
}
