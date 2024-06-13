package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.receipt.TransactionResponseDTO;
import com.bni.finproajubackend.interfaces.ReceiptInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/private")
public class ReceiptController {

    @Autowired
    private TemplateResInterface responseService;
    @Autowired
    private ReceiptInterface transactionService;

    @GetMapping(value = "/customer/transaction/{id}/receipt", produces="application/json")
    public ResponseEntity updateTicketStatus(@PathVariable Long id, Authentication authentication) {
        try {
            TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionDetails(id, authentication);
            return ResponseEntity.ok(responseService.apiSuccess(transactionResponseDTO, "Successfully updated ticket status."));
        } catch (EntityNotFoundException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiNotFound(null, err.getCause() == null ? "Something went wrong" : err.getMessage()));
        }catch (Exception err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseService.apiFailed(null, err.getCause() == null ? "Something went wrong" : err.getMessage()));
        }
    }

}
