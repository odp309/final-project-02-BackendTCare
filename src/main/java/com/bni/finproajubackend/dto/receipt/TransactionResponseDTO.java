package com.bni.finproajubackend.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponseDTO {
    private long amount;
    private String transaction_number;
    private String transaction_date;
    private String transaction_type;
    private String transaction_category;
    private String sender_account_number;
    private String sender_name;
    private String sender_company;
    private String recipient_account_number;
    private String recipient_name;
    private String recipient_company;
}
