package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.TicketStatusResponseDTO;
import com.bni.finproajubackend.dto.TicketStatusUpdateDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private/admin")
public class TicketController {

    @Autowired
    private TicketService ticketService;

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
}
