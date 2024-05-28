package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.permission.PermissionRequestDTO;
import com.bni.finproajubackend.dto.permission.PermissionResponseDTO;
import com.bni.finproajubackend.interfaces.PermissionInterface;
import com.bni.finproajubackend.model.user.admin.Permission;
import com.bni.finproajubackend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionInterface {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<PermissionResponseDTO> getPermissions() {
        List<Permission> permissions = permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "permissionName"));
        return permissions.stream()
                .map(role -> {
                    return PermissionResponseDTO.builder()
                            .message("Success get all role")
                            .permissionName(role.getPermissionName())
                            .permissionDescription(role.getPermissionDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponseDTO createNewPermission(PermissionRequestDTO permissionRequestDTO) {
        Permission permission = new Permission();
        permission.setPermissionName(permissionRequestDTO.getPermissionName());
        permission.setPermissionDescription(permissionRequestDTO.getPermissionDescription());
        permissionRepository.save(permission);
        return PermissionResponseDTO.builder()
                .message("Success create a new role")
                .permissionName(permission.getPermissionName())
                .permissionDescription(permission.getPermissionDescription())
                .build();
    }

    @Override
    public PermissionResponseDTO updatePermission(long id, PermissionRequestDTO request) {
        Permission permission = permissionRepository.findById(id);
        if (permission == null)
            throw new NotFoundException("Roles Is Not Exist in Database");
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionDescription(request.getPermissionDescription());
        permissionRepository.save(permission);
        return PermissionResponseDTO.builder()
                .message("Success updating role")
                .permissionName(permission.getPermissionName())
                .permissionDescription(permission.getPermissionDescription())
                .build();
    }
}
