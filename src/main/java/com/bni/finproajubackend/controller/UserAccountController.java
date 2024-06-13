package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.userAccount.ListAccountNumberDTO;
import com.bni.finproajubackend.dto.userAccount.UserAccountDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.service.UserAccountService;
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
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private TemplateResInterface responseService;
    private Map<String, Object> errorDetails = new HashMap<>();

    //@RequiresPermission("getUserAccount")
    @GetMapping(value = "/customer/homepage", produces = "application/json")
    public ResponseEntity getUserAccountDetail(Authentication authentication){
        try {
            UserAccountDTO userAccountDTO = userAccountService.getUserAccount(authentication);
            if(userAccountDTO == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null,"Data not found"));
            return ResponseEntity.ok(responseService.apiSuccess(userAccountDTO, "Success get data"));
        }
        catch (Exception e){
            errorDetails.put("message", e.getCause()== null ? "Not Permitted": e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong" : e.getMessage()));
        }
    }

    @GetMapping(value = "/customer/account-numbers", produces = "application/json")
    public ResponseEntity getMyAccountNumberList(Authentication authentication){
        try {
            ListAccountNumberDTO listAccountNumberDTO = userAccountService.getMyAccountNumber(authentication);
            if(listAccountNumberDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, "Data not found!"));
            }
            return ResponseEntity.ok(responseService.apiSuccess(listAccountNumberDTO, "Success"));
        }
        catch (Exception e){
            errorDetails.put("message", e.getCause()== null ? "Not Permitted": e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong" : e.getMessage()));
        }
    }
}
