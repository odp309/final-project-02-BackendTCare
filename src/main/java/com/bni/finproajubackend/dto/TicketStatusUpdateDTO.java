package com.bni.finproajubackend.dto;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TicketStatusUpdateDTO {
    private TicketStatus status;
}
