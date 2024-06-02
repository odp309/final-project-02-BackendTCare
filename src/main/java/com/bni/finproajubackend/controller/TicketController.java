package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/private/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;


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

    @RequiresPermission("updateStatus")
    @PutMapping(value = "/{ticket}/update-status", produces = "application/json")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(@PathVariable Long ticketId, @RequestParam Status newStatus) {
        Tickets tickets = ticketService.getTicketDetails(ticketId);

        if (tickets == null) {
            return ResponseEntity.notFound().build();
        }

        tickets.setTicketStatus(newStatus);

        ticketService.updateTicketStatus(ticketId, newStatus);

        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setTicketNumber(tickets.ticketNumber());
        responseDTO.setTicketStatus(tickets.ticketStatus());
        responseDTO.setTicketCategoryId(tickets.ticketCategory());

        return ResponseEntity.ok(responseDTO);
    }
}
