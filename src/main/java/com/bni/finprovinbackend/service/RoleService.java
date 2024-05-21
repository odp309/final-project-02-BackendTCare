package com.bni.finprovinbackend.service;

import com.bni.finprovinbackend.dto.role.RoleRequestDTO;
import com.bni.finprovinbackend.dto.role.RoleResponseDTO;
import com.bni.finprovinbackend.interfaces.RoleInterface;
import com.bni.finprovinbackend.model.user.admin.Role;
import com.bni.finprovinbackend.repository.RoleRepository;
import com.bni.finprovinbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleInterface {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RoleResponseDTO> getRoles() {
        List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "roleName"));
        return roles.stream()
                .map(role -> {
                    return RoleResponseDTO.builder()
                            .message("Success get all role")
                            .roleName(role.getRoleName())
                            .roleDescription(role.getRoleDescription())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO createNewRole(RoleRequestDTO roleRequestDTO) {
        Role role = new Role();
        role.setRoleName(roleRequestDTO.getRoleName());
        role.setRoleDescription(roleRequestDTO.getRoleDescription());
        roleRepository.save(role);
        return RoleResponseDTO.builder()
                .message("Success create a new role")
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }

    @Override
    public RoleResponseDTO updateRole(long id, RoleRequestDTO request) {
        Role role = roleRepository.findById(id);
        if (role == null)
            throw new NotFoundException("Roles Is Not Exist in Database");
        role.setRoleName(request.getRoleName());
        role.setRoleDescription(request.getRoleDescription());
        roleRepository.save(role);
        return RoleResponseDTO.builder()
                .message("Success updating role")
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }
}
