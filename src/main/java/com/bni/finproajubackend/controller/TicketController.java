package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.TicketStatusResponseDTO;
import com.bni.finproajubackend.dto.TicketStatusUpdateDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private")
public class TicketController {

    @Autowired
    private TicketInterface ticketService;
    @Autowired
    private TemplateResInterface responseService;
    private Map<String, Object> errorDetails = new HashMap<>();

    @RequiresPermission("admin")
    @PatchMapping("/{id}/update-status")
    public ResponseEntity<TemplateResponseDTO<TicketStatusResponseDTO>> updateTicketStatus(@PathVariable Long id, @RequestBody TicketStatusUpdateDTO dto, Authentication authentication) {
        try {
            Tickets result = ticketService.updateTicketStatus(id, dto.getStatus(), authentication);
            TicketStatus ticketStatus = result.getTicketStatus();
            TicketStatusResponseDTO responseDTO = new TicketStatusResponseDTO(ticketStatus);
            return ResponseEntity.ok(responseService.apiSuccess(responseDTO, "Successfully updated ticket status."));
        } catch (Exception err) {
            errorDetails.put("message", err.getCause() == null ? "Not Permitted" : err.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, err.getCause() == null ? "Something went wrong" : err.getMessage()));
        }
    }
    @PostMapping(value = "/ticket/create", produces = "application/json")
    public ResponseEntity createNewTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        try {
            TicketResponseDTO result = ticketService.createNewTicket(ticketRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Ticket Created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Ticket Failed Created" : e.getMessage()));
        }
    }
    @GetMapping("/admin/ticket-reports")
    public ResponseEntity getAllTickets(
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
    ) {
        try {
            PaginationDTO<TicketResponseDTO> result = ticketService.getAllTickets(category, rating, status, start_date, end_date, ticket_number, page, limit, sort_by, order);
            return ResponseEntity.ok(responseService.apiSuccessPagination(result, "Success get ticket details"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.apiFailed(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getMessage()));
        }
    }

    @GetMapping("/admin/ticket-reports/:id/reporter-detail")
    public ResponseEntity getTicketDetails(@PathVariable String ticketNumber) {
        try {
            TicketResponseDTO result = ticketService.getTicketDetails(ticketNumber);
            if (result == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
}
