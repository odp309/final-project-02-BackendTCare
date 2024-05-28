package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.permission.PermissionRequestDTO;
import com.bni.finprovinbackend.dto.permission.PermissionResponseDTO;

import java.util.List;

public interface PermissionInterface {
    List<PermissionResponseDTO> getPermissions();

    PermissionResponseDTO createNewPermission(PermissionRequestDTO request);

    PermissionResponseDTO updatePermission(long id, PermissionRequestDTO request);
}
