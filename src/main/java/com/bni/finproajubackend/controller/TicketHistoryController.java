package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.dto.tickets.TrackTicketStatusResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.service.TicketHistoryStatusService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TicketHistoryController {
    @Autowired
    private TemplateResInterface responseService;
    @Autowired
    private TicketHistoryStatusService ticketHistoryStatusService;
    @Autowired
    private Map<String, Object> errorDetails = new HashMap<>();

    @GetMapping(value = "/admin/ticket-reports/{id}/track-report-status", produces = "application/json")
    public ResponseEntity<TemplateResponseDTO<List<TrackTicketStatusResponseDTO>>> getTicketHistoryDetail(@PathVariable Long id) {
        try {
            List<TrackTicketStatusResponseDTO> responseDTOList = ticketHistoryStatusService.trackTicketStatus(id);
            return ResponseEntity.ok(responseService.apiSuccess(responseDTOList, "Success"));
        } catch (EntityNotFoundException e) {
            errorDetails.put("message", "Ticket not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Ticket not found"));
        } catch (Exception err) {
            errorDetails.put("message", err.getMessage());
            err.printStackTrace();  // Print stack trace to debug the exact cause
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseService.apiFailed(null, "Something went wrong: " + err.getMessage()));
        }
    }

    @GetMapping(value = "/customer/ticket-reports/{id}/track-report-status", produces = "application/json")
    public ResponseEntity<TemplateResponseDTO<List<TrackTicketStatusResponseDTO>>> getMyTicketHistoryDetail(Authentication authentication, @PathVariable Long id, @RequestParam String account_number){
        try{
            List<TrackTicketStatusResponseDTO> responseDTOList = ticketHistoryStatusService.trackMyTicketStatus(authentication, id, account_number);
            return ResponseEntity.ok(responseService.apiSuccess(responseDTOList, "Success"));
        }
        catch (Exception e){
            errorDetails.put("message", "Ticket not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Ticket not found"));
        }
    }

}
