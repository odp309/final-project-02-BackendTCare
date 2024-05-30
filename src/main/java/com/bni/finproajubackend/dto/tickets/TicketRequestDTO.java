package com.bni.finproajubackend.dto.tickets;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketRequestDTO {

    private String ticketNumber;
    private Long transactionId;
    private Long ticketCategoryId;
    private Long status;
    private String description;
    private LocalDateTime responseTime;

}