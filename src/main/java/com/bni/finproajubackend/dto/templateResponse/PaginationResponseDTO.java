package com.bni.finproajubackend.dto.templateResponse;

import lombok.Data;

@Data
public class PaginationResponseDTO extends StatusResponseDTO {
    private int currentPage;
    private int currentItem;
    private int totalPage;
    private long totalItem;
}
