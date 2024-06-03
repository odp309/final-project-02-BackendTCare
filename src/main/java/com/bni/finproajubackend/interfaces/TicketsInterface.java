package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TicketsHistoryResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TicketsInterface {
    void createTicketsHistory(long id, Authentication authentication) throws Exception;
    List<TicketsHistoryResponseDTO> getTicketsHistory(long id);
}
