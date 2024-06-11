package com.bni.finproajubackend.dto.userAccount;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private String transaction_description;
    private String transaction_type;
    private String date;
    private String ticket_status;

}