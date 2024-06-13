package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintResponseDTO {
    private long transaction_id;
    private String account_number;
    private String ticket_category;
    private boolean reopen_ticket;
    private String reference_number;
}
