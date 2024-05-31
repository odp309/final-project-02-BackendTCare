package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/private/ticket")
public class TicketController {

    @Autowired
    private final TicketService ticketService;

    @Autowired
    private TemplateResInterface responseService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @RequiresPermission("getAllTicket")
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<Tickets> tickets = ticketService.getAllTickets();

        if (tickets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<TicketResponseDTO> responseDTOs = new ArrayList<>();
        for (Tickets ticket : tickets) {
            TicketResponseDTO responseDTO = new TicketResponseDTO();
            responseDTO.setTicketNumber(ticket.getTicketNumber());
            responseDTO.setTicketStatus(ticket.getTicketStatus());
            responseDTO.setTicketCategoryId(ticket.getTicketCategoryId());

            responseDTOs.add(responseDTO);
        }
        return ResponseEntity.ok(responseDTOs);
    }

    @RequiresPermission("getTicketDetail")
    @GetMapping(value = "/ticket", produces = "application/json")
    public ResponseEntity<TicketResponseDTO> getTicketDetails(@PathVariable Long ticketId) {
        Tickets tickets = ticketService.getTicketDetails(ticketId);

        if (tickets == null) {
            return ResponseEntity.notFound().build();
        }

        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setTicketNumber(tickets.getTicketNumber());
        responseDTO.setTicketStatus(tickets.getTicketStatus());
        responseDTO.setTicketCategoryId(tickets.getTicketCategoryId());

        return ResponseEntity.ok(responseDTO);
    }

    @RequiresPermission("addTicket")
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO requestDTO) {
        if (requestDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        Tickets tickets = ticketService.createTicket(requestDTO);

        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setTicketNumber(tickets.getTicketNumber());
        responseDTO.setTicketStatus(tickets.getTicketStatus());
        responseDTO.setTicketCategoryId(tickets.getTicketCategoryId());

        return ResponseEntity.ok(responseDTO);
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
        responseDTO.setTicketNumber(tickets.getTicketNumber());
        responseDTO.setTicketStatus(tickets.getTicketStatus());
        responseDTO.setTicketCategoryId(tickets.getTicketCategoryId());

        return ResponseEntity.ok(responseDTO);
    }
}
