package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import com.bni.finproajubackend.interfaces.AdminInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements AdminInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public AdminResponseDTO getAdminProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new RuntimeException("User not found");

        if (user.getAdmin() == null)
            throw new RuntimeException("Admin data not found for user: " + username);

        return AdminResponseDTO.builder()
                .id(user.getId())
                .email(user.getAdmin().getEmail())
                .noHP(user.getAdmin().getNoHP())
                .firstName(user.getAdmin().getFirstName())
                .lastName(user.getAdmin().getLastName())
                .gender(user.getAdmin().getGender())
                .age(user.getAdmin().getAge())
                .address(user.getAdmin().getAddress())
                .npp(user.getAdmin().getNpp())  // Include npp here
                .role(user.getAdmin().getRole())  // Include RoleDTO here
                .build();
    }
}
