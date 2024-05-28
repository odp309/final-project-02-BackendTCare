package com.bni.finprovinbackend.controller;

import com.bni.finprovinbackend.annotation.RequiresPermission;
import com.bni.finprovinbackend.dto.role.RoleRequestDTO;
import com.bni.finprovinbackend.dto.role.RoleResponseDTO;
import com.bni.finprovinbackend.interfaces.RoleInterface;
import com.bni.finprovinbackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get list of role"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @RequiresPermission("addRoles")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            RoleResponseDTO result = roleService.createNewRole(roleRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "New Role Crated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @RequiresPermission("updateRoles")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity updateRole(@PathVariable long id, @RequestBody RoleRequestDTO roleRequestDTO) {
        try {
            RoleResponseDTO result = roleService.updateRole(id, roleRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Role Updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
}
