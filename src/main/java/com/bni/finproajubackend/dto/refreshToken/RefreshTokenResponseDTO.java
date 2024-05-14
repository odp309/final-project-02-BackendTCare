package com.bni.finproajubackend.dto.refreshToken;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshTokenResponseDTO {
    private String accessToken, token;
}
