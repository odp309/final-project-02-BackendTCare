package com.bni.finprovinbackend.dto.refreshToken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RefreshTokenRequestDTO {
    private String token;
}
