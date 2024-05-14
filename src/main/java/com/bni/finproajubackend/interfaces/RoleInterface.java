package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
import com.bni.finproajubackend.model.user.admin.Role;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface RoleInterface {
    List<RoleResponseDTO> getRoles(Authentication authentication);

    RoleResponseDTO createNewRole(RoleRequestDTO request);

    RoleResponseDTO updateRole(RoleRequestDTO request);
}
