package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TicketRequestDTO {

    private Long transactionId;
    private TicketCategories ticketCategory;
    private TicketStatus status;
    private String description;

}