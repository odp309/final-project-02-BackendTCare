package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.permission.PermissionRequestDTO;
import com.bni.finproajubackend.dto.permission.PermissionResponseDTO;

import java.util.List;

public interface PermissionInterface {
    List<PermissionResponseDTO> getPermissions();

    PermissionResponseDTO createNewPermission(PermissionRequestDTO request);

    PermissionResponseDTO updatePermission(long id, PermissionRequestDTO request);
}
