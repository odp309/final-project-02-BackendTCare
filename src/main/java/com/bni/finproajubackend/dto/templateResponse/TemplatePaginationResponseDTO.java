package com.bni.finproajubackend.dto.templateResponse;

import lombok.Data;

import java.util.List;

@Data
public class TemplatePaginationResponseDTO<T> extends PaginationResponseDTO {
    private List<T> result;
}
