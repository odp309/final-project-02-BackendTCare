package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.role.RoleRequestDTO;
import com.bni.finprovinbackend.dto.role.RoleResponseDTO;

import java.util.List;

public interface RoleInterface {
    List<RoleResponseDTO> getRoles();

    RoleResponseDTO createNewRole(RoleRequestDTO request);

    RoleResponseDTO updateRole(long id, RoleRequestDTO request);
}
