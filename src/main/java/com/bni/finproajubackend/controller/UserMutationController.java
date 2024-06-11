package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.UserInterface;
import com.bni.finproajubackend.service.UserMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

