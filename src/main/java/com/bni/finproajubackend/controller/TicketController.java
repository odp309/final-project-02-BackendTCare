package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.dto.tickets.*;
import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.TicketStatusResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.TicketReportsInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketInterface ticketService;
    @Autowired
    private TicketReportsInterface ticketReportsService;
    @Autowired
    private TemplateResInterface responseService;
    private Map<String, Object> errorDetails = new HashMap<>();


    @RequiresPermission("admin")
    @PatchMapping("/admin/ticket-reports/{id}/update-status")
    public ResponseEntity<TemplateResponseDTO<TicketStatusResponseDTO>> updateTicketStatus(@PathVariable Long id, Authentication authentication) {
        try {
            Tickets result = ticketService.updateTicketStatus(id, authentication);
            TicketStatus ticketStatus = result.getTicketStatus();
            TicketStatusResponseDTO responseDTO = new TicketStatusResponseDTO(ticketStatus);
            return ResponseEntity.ok(responseService.apiSuccess(responseDTO, "Successfully updated ticket status."));
        } catch (Exception err) {
            errorDetails.put("message", err.getCause() == null ? "Not Permitted" : err.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, err.getCause() == null ? "Something went wrong" : err.getMessage()));
        }
    }

    @PostMapping(value = "/customer/transaction/complaint-form", produces = "application/json")
    public ResponseEntity createNewTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        try {
            TicketResponseDTO result = ticketService.createNewTicket(ticketRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Ticket Created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Ticket Failed Created" : e.getMessage()));
        }
    }

    @GetMapping("/{user}/ticket-reports")
    public ResponseEntity getAllTickets(
            @PathVariable String user,
            @RequestParam(required = false) String account_number,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(required = false) String created_at,
            @RequestParam(required = false) String division,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "createdAt") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order,
            Authentication authentication
    ) {
        try {
            PaginationDTO result = ticketReportsService.getAllTickets(user, account_number, category, rating, status, start_date, end_date, ticket_number, created_at, division, page, limit, sort_by, order, authentication);
            return ResponseEntity.ok(responseService.apiSuccessPagination(result, "Success get ticket details"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.apiFailed(null, e.getMessage()));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(responseService.apiUnauthorized(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getMessage()));
        }
    }

    @GetMapping("/admin/ticket-reports/{ticket_number}/reporter-detail")
    public ResponseEntity getTicketDetails(@PathVariable String ticket_number) {
        try {
            TicketDetailsReportDTO result = ticketService.getTicketDetails(ticket_number);
            if (result == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @GetMapping("/customer/ticket-reports/{ticket_number}/reporter-detail")
    public ResponseEntity getCustomerTicketDetails(@PathVariable String ticket_number) {
        try {
            CustomerTicketDetailsReportDTO result = ticketService.getCustomerTicketDetails(ticket_number);
            if (result == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @GetMapping(value = "/customer/transaction/{id}/complaint-form", produces = "application/json")
    public ResponseEntity getFormComplaint(@PathVariable Long id) {
        try {
            ComplaintResponseDTO result = ticketService.getFormComplaint(id);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiNotFound(null, e.getCause() == null ? "Something went wrong" : e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
    @GetMapping("/admin/ticket-reports/{id}/feedback")
    public ResponseEntity<?> getTicketFeedback(@PathVariable("id") Long ticket_id) {
        try {
            TicketFeedbackResponseDTO result = ticketService.getTicketFeedback(ticket_id);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket feedback"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getMessage()));
        }
    }

    @GetMapping("/customer/ticket-reports/{id}/feedback")
    public ResponseEntity<?> getCustomerTicketFeedback(@PathVariable("id") Long ticket_id) {
        try {
            CustomerTicketDetailsReportDTO result = ticketService.getCustomerTicketDetails(ticket_id);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket feedback"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getMessage()));
        }
    }
}
