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
    TicketResponseDTO getTicketDetails(Long ticketId);
    Tickets createTicketsHistory(long id, Authentication authentication) throws Exception;
    void createAndSaveTicketHistory(long level, Tickets tickets, Admin admin, String description);
    Tickets updateTicketStatus(Long id, TicketStatus status);
    List<TicketHistoryResponseDTO> getTicketHistory(long id);
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO);
    String getAdminFullName(@NotNull Admin admin);
}
