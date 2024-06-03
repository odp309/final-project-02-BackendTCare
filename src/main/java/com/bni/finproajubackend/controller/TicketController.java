package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/private/ticket")
public class TicketController {

    @Autowired
    private TicketInterface ticketService;


    @GetMapping("/ticket")
    public ResponseEntity<List<Tickets>> getAllTickets() {
        List<Tickets> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok().body(tickets);
    }

    @GetMapping("/detail")
    public ResponseEntity<Tickets> getTicketDetails(@RequestParam("ticketNumber") Long Id) {
        Tickets ticketDetail = ticketService.getTicketDetails(Id);
        if(ticketDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ticketDetail);
    }

    @PostMapping(value = "/ticket/create", produces = "application/json")
    public ResponseEntity<Tickets> createTicket(@RequestBody TicketRequestDTO requestDTO) {
        Tickets createdTicket = ticketService.createTicket(requestDTO);
        return ResponseEntity.ok().body(createdTicket);
    }

    @PutMapping(value = "/ticket/{Id}/status", produces = "application/json")
    public ResponseEntity<Tickets> updateTicketStatus(@PathVariable Long Id, @RequestBody Status newStatus) {
        Tickets updatedTicket = ticketService.updateTicketStatus(Id, newStatus);
        return ResponseEntity.ok().body(updatedTicket);
    }
}
