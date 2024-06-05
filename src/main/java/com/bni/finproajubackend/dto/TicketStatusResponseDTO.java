package com.bni.finproajubackend.dto;

import com.bni.finproajubackend.model.enumobject.TicketStatus;

public class TicketStatusResponseDTO {
    private TicketStatus status;

    // Konstruktor default
    public TicketStatusResponseDTO() {
    }

    // Konstruktor dengan parameter
    public TicketStatusResponseDTO(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
