package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TrackTicketStatusResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TrackTicketStatusInterface {
    List<TrackTicketStatusResponseDTO> trackTicketStatus(Long id);
    List<TrackTicketStatusResponseDTO> trackMyTicketStatus(Authentication authentication, Long id, String account_number);

}
