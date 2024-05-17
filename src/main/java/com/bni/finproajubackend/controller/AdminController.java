package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import com.bni.finproajubackend.interfaces.AdminInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private/admin")
public class AdminController {

    @Autowired
    private AdminInterface adminService;
    @Autowired
    private TemplateResInterface responseService;

    private Map<String, Object> errorDetails = new HashMap<>();

    @RequiresPermission("getAdminProfile")
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity getDetailAdmin(Authentication authentication) {
        try {
            AdminResponseDTO adminResponseDTO = adminService.getAdminProfile(authentication);;
            if (adminResponseDTO == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed("Data not Found"));
            return ResponseEntity.ok(responseService.apiSuccess(adminResponseDTO));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Permitted" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }
}
