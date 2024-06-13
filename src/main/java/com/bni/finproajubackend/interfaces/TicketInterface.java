package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException;

    CustomerTicketDetailsReportDTO getCustomerTicketDetails(String ticketNumber);

    TicketDetailsReportDTO getTicketDetails(String id);

    String createTicketNumber(Transaction transaction);

    List<TicketHistoryResponseDTO> getTicketHistory(long id);

    TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO);
    String getAdminFullName(@NotNull Admin admin);

    ComplaintResponseDTO getFormComplaint(long id) throws Exception;
}
