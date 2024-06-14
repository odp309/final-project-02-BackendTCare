package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.UserInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.service.UserMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private")
public class UserMutationController {
    @Autowired
    private UserMutationService userMutationService;
    @Autowired
    private TemplateResInterface responseService;
    private Map<String, Object> errorDetails = new HashMap<>();

    @GetMapping(value = "/customer/history-transaction", produces = "application/json")
    public ResponseEntity getUserMutationDetail(Authentication authentication){
        try {
            UserMutationDTO userMutationDTO = userMutationService.getUserMutations(authentication);
            if (userMutationDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Data not found!"));
            }
            return ResponseEntity.ok(responseService.apiSuccess(userMutationDTO, "Success!"));
        }catch (Exception e){
            errorDetails.put("message", e.getCause()== null ? "Not Permitted": e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong" : e.getMessage()));
        }
    }
//    @GetMapping(value = "/customer/list-transaction", produces = "application/json")
//    public ResponseEntity getUserListTransactions(Authentication authentication, @RequestParam String account_number){
//        try {
//            UserMutationDTO userMutationDTO = userMutationService.getUserListTransaction(authentication, account_number);
//            if(userMutationDTO == null){
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Data not found"));
//            }
//            return ResponseEntity.ok(responseService.apiSuccess(userMutationDTO, "Success"));
//        }catch (Exception e){
//            errorDetails.put("message", e.getCause()==null ? "Not Permitted" : e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong!" : e.getMessage()));
//        }
//    }

    @GetMapping(value = "/customer/list-transaction", produces = "application/json")
    public ResponseEntity getUserListTransactions(Authentication authentication,
                                                  @RequestParam String account_number,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start_date,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end_date,
                                                  @RequestParam(required = false) TicketStatus ticket_status) {
        try {
            UserMutationDTO userMutationDTO = userMutationService.getUserListTransaction(authentication, account_number, start_date, end_date, ticket_status);
            if (userMutationDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Data not found"));
            }
            return ResponseEntity.ok(responseService.apiSuccess(userMutationDTO, "Success"));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Permitted" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Something went wrong!" : e.getMessage()));
        }
    }


    @GetMapping(value = "/customer/account-transaction", produces = "application/json")
    public ResponseEntity getUserTransactionsByAccountNo(Authentication authentication,
                                                         @RequestParam String account_number,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start_date,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end_date){
        try {
            UserMutationDTO userMutationDTO = userMutationService.getUserTransactionsByAccountNo(authentication, account_number, start_date, end_date);
            if(userMutationDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Data not found"));
            }
            return ResponseEntity.ok(responseService.apiSuccess(userMutationDTO, "Success"));
        }catch (Exception e){
            errorDetails.put("message", e.getCause()==null ? "Not Permitted" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong!" : e.getMessage()));
        }
    }

}
