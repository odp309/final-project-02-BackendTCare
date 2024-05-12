package com.bni.finalproject01webservice.interfaces;

import com.bni.finalproject01webservice.dto.LoginRequestDTO;
import com.bni.finalproject01webservice.dto.LoginResponseDTO;
import com.bni.finalproject01webservice.dto.RegisterRequestDTO;
import com.bni.finalproject01webservice.dto.RegisterResponseDTO;

public interface UserInterface {

    void initRoleAndUser();

    LoginResponseDTO login(LoginRequestDTO request);

    RegisterResponseDTO register(RegisterRequestDTO request);
}
