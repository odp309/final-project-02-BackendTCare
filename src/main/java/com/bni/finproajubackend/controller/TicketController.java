package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
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

    @GetMapping("/all")
    public ResponseEntity getAllTickets() {
        try {
            List<TicketResponseDTO> result = ticketService.getAllTickets();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get list of tickets"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Internal Server Error" : e.getMessage()));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity getTicketDetails(@RequestParam("ticketNumber") Long Id) {
        Tickets ticketDetail = ticketService.getTicketDetails(Id);
        if(ticketDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ticketDetail);
    }

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity createNewTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        try {
            TicketResponseDTO result = ticketService.createNewTicket(ticketRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Ticket Created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Ticket Failed Created" : e.getMessage()));
        }
    }
}
