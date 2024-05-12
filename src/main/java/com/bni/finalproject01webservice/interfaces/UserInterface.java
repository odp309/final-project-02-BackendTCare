package com.bni.finalproject01webservice.interfaces;

import com.bni.finalproject01webservice.dto.*;
import org.springframework.http.ResponseEntity;

public interface UserInterface {

    InitResponseDTO initRoleAndUser();

    LoginResponseDTO login(LoginRequestDTO request);

    RegisterResponseDTO register(RegisterRequestDTO request);
}
