package com.bni.finproajubackend.dto.tickets;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketsRequestDTO {

    private String ticketNumber;
    private Long transactionId;
    private Long category;
    private Long status;
    private String description;
    private LocalDateTime responseTime;

}