package com.bni.finproajubackend.dto.userAccount;

import lombok.Data;

import java.util.List;

@Data
public class AccountDTO {
    private Long id;
    private String account_number;
    private Long balance;
    private String type;
    private int amount;
    //private List<TransactionDTO> transactionList;
}
