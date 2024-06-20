package com.bni.finproajubackend.dto.userAccount;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transaction_description;
    private String transaction_type;
    private Long balance;
    private String date;
    private String ticket_status;
    private String ticket_number;

}