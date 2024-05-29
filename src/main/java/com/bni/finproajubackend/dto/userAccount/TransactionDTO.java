package com.bni.finproajubackend.dto.userAccount;

import lombok.Data;

@Data
public class TransactionDTO {
    private Long id;
    private String transactionDetail;
    private String status;

}
