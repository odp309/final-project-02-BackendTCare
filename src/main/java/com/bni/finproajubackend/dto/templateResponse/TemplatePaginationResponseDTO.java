package com.bni.finproajubackend.dto.templateResponse;

import lombok.Data;

import java.util.List;

@Data
public class TemplatePaginationResponseDTO<T> extends PaginationResponseDTO {
    private T result;
}
