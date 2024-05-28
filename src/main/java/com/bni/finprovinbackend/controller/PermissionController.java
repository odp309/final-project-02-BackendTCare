package com.bni.finprovinbackend.controller;

import com.bni.finprovinbackend.annotation.RequiresPermission;
import com.bni.finprovinbackend.dto.permission.PermissionRequestDTO;
import com.bni.finprovinbackend.dto.permission.PermissionResponseDTO;
import com.bni.finprovinbackend.interfaces.PermissionInterface;
import com.bni.finprovinbackend.interfaces.TemplateResInterface;
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
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }

    @RequiresPermission("addPermissions")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewPermission(@RequestBody PermissionRequestDTO PermissionRequestDTO) {
        try {
            PermissionResponseDTO result = PermissionService.createNewPermission(PermissionRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }

    @RequiresPermission("updatePermissions")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity updatePermission(@PathVariable long id, @RequestBody PermissionRequestDTO PermissionRequestDTO) {
        try {
            PermissionResponseDTO result = PermissionService.updatePermission(id, PermissionRequestDTO);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            errorDetails.put("message", e.getCause() == null ? "Not Found" : e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(errorDetails));
        }
    }
}
