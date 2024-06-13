package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketsNasabahResponseDTO {
    private long id;
    private String ticket_number;
    private String transaction_type;
    private String ticket_date;
    private String ticket_description;
    private long amount;
    private String ticket_status;
}
