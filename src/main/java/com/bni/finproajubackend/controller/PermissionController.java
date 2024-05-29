package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.permission.PermissionRequestDTO;
import com.bni.finproajubackend.dto.permission.PermissionResponseDTO;
import com.bni.finproajubackend.interfaces.PermissionInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/private/permission")
public class PermissionController {
    @Autowired
    private PermissionInterface PermissionService;
    @Autowired
    private TemplateResInterface responseService;

    private Map<String, Object> errorDetails = new HashMap<>();

    @RequiresPermission("getPermissions")
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getPermissions() {
        try {
            List<PermissionResponseDTO> result = PermissionService.getPermissions();
            return ResponseEntity.ok(responseService.apiSuccess(result, "Success get list of permission"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @RequiresPermission("addPermissions")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewPermission(@RequestBody PermissionRequestDTO PermissionRequestDTO) {
        try {
            PermissionResponseDTO result = PermissionService.createNewPermission(PermissionRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "New Permission Created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }

    @RequiresPermission("updatePermissions")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity updatePermission(@PathVariable long id, @RequestBody PermissionRequestDTO PermissionRequestDTO) {
        try {
            PermissionResponseDTO result = PermissionService.updatePermission(id, PermissionRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Permission Updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause() == null ? "Not Found" : e.getMessage()));
        }
    }
}
