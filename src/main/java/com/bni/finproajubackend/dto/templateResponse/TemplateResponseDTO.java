package com.bni.finproajubackend.dto.templateResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TemplateResponseDTO<T> extends StatusResponseDTO {
    private T result;
}
