package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListTicketNasabahResponseDTO{
    private String account_number;
    private List<TicketsNasabahResponseDTO> list_tickets;
}
