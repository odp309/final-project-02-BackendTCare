package com.bni.finproajubackend.dto.userAccount;

import lombok.Data;

import java.util.List;

@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private Long balance;
    private String type;
    //private List<TransactionDTO> transactionList;
}
