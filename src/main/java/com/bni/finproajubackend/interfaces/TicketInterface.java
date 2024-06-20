package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface TicketInterface {
    Tickets updateTicketStatus(Long ticketId, Authentication authentication) throws MessagingException;

    CustomerTicketDetailsReportDTO getCustomerTicketDetails(String ticketNumber);

    TicketDetailsReportDTO getTicketDetails(String id);

    String createTicketNumber(Transaction transaction);

    List<TicketHistoryResponseDTO> getTicketHistory(long id);

    TicketResponseDTO createNewTicket(Long id, TicketRequestDTO ticketRequestDTO) throws Exception;

    String getAdminFullName(@NotNull Admin admin);

    ComplaintResponseDTO getFormComplaint(long id) throws Exception;

    TicketFeedbackResponseDTO getTicketFeedback(Long ticket_id);

    CustomerTicketFeedbackResponseDTO getCustomerTicketFeedback(String ticket_number);

    CreateFeedbackResponseDTO createFeedback(CreateFeedbackRequestDTO requestDTO,
                                             String ticket_number,
                                             Authentication authentication);

}
