package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.role.RoleRequestDTO;
import com.bni.finproajubackend.dto.role.RoleResponseDTO;
import com.bni.finproajubackend.interfaces.RoleInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Role;
import com.bni.finproajubackend.repository.RoleRepository;
import com.bni.finproajubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
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
//        User currUser = userRepository.findByUsername(authentication.getName());
//        if (!currUser.getPerson().getAdmin().getRole().getPermissions().contains("getRoles"))
//            throw new NotFoundException("User Not Permit To See Roles");
        List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "role_name"));
        return roles.stream()
                .map(role -> {
                    return RoleResponseDTO.builder()
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
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }

    @Override
    public RoleResponseDTO updateRole(RoleRequestDTO request) {
        Role role = roleRepository.findByRoleName(request.getRoleName());
        if (role == null)
            throw new NotFoundException("Roles Is Not Exist in Database");
        role.setRoleName(request.getRoleName());
        role.setRoleDescription(request.getRoleDescription());
        roleRepository.save(role);
        return RoleResponseDTO.builder()
                .roleName(role.getRoleName())
                .roleDescription(role.getRoleDescription())
                .build();
    }
}
