package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.TicketStatusUpdateDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")

public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TemplateResInterface responseService;
    @RequiresPermission("admin")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TemplateResponseDTO<Tickets>> updateTicketStatus(@PathVariable Long id, @RequestBody TicketStatusUpdateDTO dto) {
        Tickets result = ticketService.updateTicketStatus(id, dto.getStatus());
        return ResponseEntity.ok(responseService.apiSuccess(result, "Successfully updated ticket status."));
    }
}
