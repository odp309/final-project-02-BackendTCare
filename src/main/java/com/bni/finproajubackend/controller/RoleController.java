package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
import com.bni.finproajubackend.interfaces.RoleInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.user.admin.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/role")
public class RoleController {

    @Autowired
    private RoleInterface roleService;
    @Autowired
    private TemplateResInterface responseService;

    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getRoles(Authentication authentication){
        List<RoleResponseDTO> result = roleService.getRoles(authentication);
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO result = roleService.createNewRole(roleRequestDTO);
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }

    @PutMapping(value = "", produces = "application/json")
    public ResponseEntity updateRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO result = roleService.updateRole(roleRequestDTO);
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }
}
