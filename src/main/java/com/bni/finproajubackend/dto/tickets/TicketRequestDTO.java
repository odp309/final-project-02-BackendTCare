package com.bni.finproajubackend.dto.tickets;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TicketRequestDTO {

    private Long transactionId;
    private Long ticketCategoryId;
    private String description;

}