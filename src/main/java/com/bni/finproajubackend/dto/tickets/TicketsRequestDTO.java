package com.bni.finproajubackend.dto.tickets;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketsRequestDTO {

    private String ticketNumber;
    private Long transactionId;
    private Long ticketCategoryId;
    private Long ticketStatusId;
    private String description;
    private LocalDateTime responseTime;

}