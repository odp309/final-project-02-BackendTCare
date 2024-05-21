package com.bni.finprovinbackend.dto.refreshToken;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshTokenResponseDTO {
    private String message, accessToken;
}
