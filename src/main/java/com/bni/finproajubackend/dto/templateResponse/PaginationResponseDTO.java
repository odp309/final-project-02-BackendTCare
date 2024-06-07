package com.bni.finproajubackend.dto.templateResponse;

import lombok.Data;

@Data
public class PaginationResponseDTO extends StatusResponseDTO {
    private int current_page;
    private int current_item;
    private int total_page;
    private long total_item;
}
