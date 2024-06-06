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
@RequestMapping("/api/v1/private/ticket")
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

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity createNewTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        try {
            TicketResponseDTO result = ticketService.createNewTicket(ticketRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Ticket Created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Ticket Failed Created" : e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAllTickets() {
        try {
            PaginationDTO<TicketResponseDTO> result = ticketService.getAllTickets(0, 20);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get list of tickets"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity getTicketDetails(@PathVariable Long ticketId) {
        try {
            TicketResponseDTO result = ticketService.getTicketDetails(ticketId);
            if (result == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
}
