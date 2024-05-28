package com.bni.finprovinbackend.dto.templateResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StatusResponseDTO {
    private HttpStatus statusCode;
    private String message;
}
