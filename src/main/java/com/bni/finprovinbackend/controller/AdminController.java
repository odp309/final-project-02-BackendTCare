package com.bni.finprovinbackend.controller;

import com.bni.finprovinbackend.annotation.RequiresPermission;
import com.bni.finprovinbackend.dto.admin.AdminResponseDTO;
import com.bni.finprovinbackend.interfaces.AdminInterface;
import com.bni.finprovinbackend.interfaces.TemplateResInterface;
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

    @RequiresPermission("getAdminProfile")
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity getDetailAdmin(Authentication authentication) {
        try {
            AdminResponseDTO adminResponseDTO = adminService.getAdminProfile(authentication);;
            if (adminResponseDTO == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null,"Data not Found"));
            return ResponseEntity.ok(responseService.apiSuccess(adminResponseDTO, "Success get admin"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Something went wrong getting profile" : e.getMessage()));
        }
    }
}
