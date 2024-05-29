package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.TicketsInterface;
import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/private/tickets")
public class TicketsController {
    @Autowired
    private TicketsInterface ticketsService;

    @Autowired
    private TemplateResInterface responseService;

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getTickets() {
        List<Tickets> listTickets = ticketsService.getTickets(authentication.getDetails());
        return ResponseEntity.ok(responseService.apiSuccess(""));
    }

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getDetailTickets(Authentication authentication) {
        List<Tickets> ticketsData = ticketsService.getTickets(authentication.getDetails());

        if (ticketsData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.apiBadRequest("Ticket Is Not Available"));
        }

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity addTickets(@RequestBody TicketsRequestDTO req, Authentication authentication) {
        try {
            Tickets result = ticketsService.createTickets(req, authentication);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(e));
        }
    }
}
