package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
import com.bni.finproajubackend.interfaces.RoleInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private/role")
public class RoleController {

    @Autowired
    private RoleInterface roleService;
    @Autowired
    private TemplateResInterface responseService;

    private Map<String, Object> errorDetails = new HashMap<>();

    @RequiresPermission("getRoles")
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getRoles() {
        try {
            List<RoleResponseDTO> result = roleService.getRoles();
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }

    @RequiresPermission("addRoles")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            RoleResponseDTO result = roleService.createNewRole(roleRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }

    @RequiresPermission("updateRoles")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity updateRole(@PathVariable long id, @RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            RoleResponseDTO result = roleService.updateRole(id, roleRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }
}
