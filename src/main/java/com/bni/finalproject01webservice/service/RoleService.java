package com.bni.finalproject01webservice.service;

import com.bni.finalproject01webservice.dto.RoleDTO;
import com.bni.finalproject01webservice.interfaces.RoleInterface;
import com.bni.finalproject01webservice.model.Role;
import com.bni.finalproject01webservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleInterface {

    private final RoleRepository roleRepository;

    @Override
    public Role createNewRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleDescription(roleDTO.getRoleDescription());

        return roleRepository.save(role);
    }
}
