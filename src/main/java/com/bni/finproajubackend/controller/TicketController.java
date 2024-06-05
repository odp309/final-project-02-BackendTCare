package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/ticket")
public class TicketController {

    @Autowired
    private TicketInterface ticketService;
    @Autowired
    private TemplateResInterface responseService;

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
            List<TicketResponseDTO> result = ticketService.getAllTickets();
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
            if (result != null) {
                return ResponseEntity.ok(responseService.apiSuccess(result, "Success get ticket details"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
}
