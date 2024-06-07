package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TrackTicketStatusResponseDTO;
import com.bni.finproajubackend.model.ticket.Tickets;

import java.util.List;

public interface TrackTicketStatusInterface {
    List<TrackTicketStatusResponseDTO> trackTicketStatus(Long id);
}
