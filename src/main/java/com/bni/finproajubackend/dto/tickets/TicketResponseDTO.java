package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {

    private long transaction_id;
    private String account_number;
    private String ticket_category;
    private Boolean reopen_ticket;
    private String reference_number;
}


