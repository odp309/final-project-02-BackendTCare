package com.bni.finproajubackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationDTO<T> {
    private List<T> result;
    private int currentPage;
    private int currentItem;
    private int totalPage;
    private long totalItem;

    // Constructor, Getter, dan Setter
}


