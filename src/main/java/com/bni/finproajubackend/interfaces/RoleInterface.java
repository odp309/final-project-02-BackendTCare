package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;

import java.util.List;

public interface RoleInterface {
    List<RoleResponseDTO> getRoles();

    RoleResponseDTO createNewRole(RoleRequestDTO request);

    RoleResponseDTO updateRole(long id, RoleRequestDTO request);
}
