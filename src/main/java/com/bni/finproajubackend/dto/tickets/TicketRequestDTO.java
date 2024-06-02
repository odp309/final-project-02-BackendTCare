package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
public class TicketRequestDTO {

    private Long id;
    private Long transaction;
    private Long ticketCategory;
    private Long status;
    private String description;
    private LocalDateTime responseTime;

}