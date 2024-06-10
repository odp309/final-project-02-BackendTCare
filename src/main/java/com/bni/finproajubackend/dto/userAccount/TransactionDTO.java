package com.bni.finproajubackend.dto.userAccount;

import lombok.Data;

@Data
public class TransactionDTO {
    private Long id;
    private String transaction_detail;
    private String transaction_type;
    private String status;
}