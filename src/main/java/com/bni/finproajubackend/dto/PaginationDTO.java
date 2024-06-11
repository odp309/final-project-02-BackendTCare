package com.bni.finproajubackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationDTO<T> {
    private T data;
    private int currentPage;
    private int currentItem;
    private int totalPage;
    private long totalItem;

    // Constructor, Getter, dan Setter
}


