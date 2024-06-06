package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, TicketStatus status, Authentication authentication);

    TicketResponseDTO getTicketDetails(String ticketNumber);

    List<TicketHistoryResponseDTO> getTicketHistory(long id);
    PaginationDTO<TicketResponseDTO> getAllTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "created_at") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order
    );
    TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO);
    String getAdminFullName(@NotNull Admin admin);
}
