package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDetail {
    private String transactionDate;
    private int amount;
    private String category;
    private String description;
    private String referenceNum;
}
