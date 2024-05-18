package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequiresPermission;
import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
import com.bni.finproajubackend.interfaces.RoleInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/role")
public class RoleController {

    @Autowired
    private RoleInterface roleService;
    @Autowired
    private TemplateResInterface responseService;

    @RequiresPermission("getRoles")
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getRoles(){
        List<RoleResponseDTO> result = roleService.getRoles();
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }

    @RequiresPermission("addRoles")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity createNewRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO result = roleService.createNewRole(roleRequestDTO);
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }

    @RequiresPermission("updateRoles")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity updateRole(@PathVariable long id, @RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO result = roleService.updateRole(id, roleRequestDTO);
        return ResponseEntity.ok(responseService.apiSuccess(result));
    }
}
