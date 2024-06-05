package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, TicketStatus status, Authentication authentication);
    TicketResponseDTO getTicketDetails(Long ticketId);
    List<TicketHistoryResponseDTO> getTicketHistory(long id);
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO);
    String getAdminFullName(@NotNull Admin admin);
}
