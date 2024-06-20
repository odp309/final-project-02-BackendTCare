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

    private Long transaction_id;
    private String comment;

}