package com.bni.finproajubackend.dto.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    private String accessToken, token;
}
