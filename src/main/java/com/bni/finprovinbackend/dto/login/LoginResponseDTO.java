package com.bni.finprovinbackend.dto.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    private String message, accessToken, token;
}
