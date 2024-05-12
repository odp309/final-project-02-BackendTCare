package com.bni.finalproject01webservice.controller;

import com.bni.finalproject01webservice.dto.*;
import com.bni.finalproject01webservice.interfaces.UserInterface;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/user")
public class UserController {

    @Autowired
    private UserInterface userService;

    @PostMapping("/init")
    public ResponseEntity<InitResponseDTO> initRoleAndUser() {
        InitResponseDTO result = userService.initRoleAndUser();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO result = userService.login(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getPrincipal();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO result = userService.register(request);
        return ResponseEntity.ok(result);
    }
}
