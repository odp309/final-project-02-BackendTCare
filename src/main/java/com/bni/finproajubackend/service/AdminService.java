package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import com.bni.finproajubackend.interfaces.AdminInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public class AdminService implements AdminInterface {

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");

    @Override
    public AdminResponseDTO getAdminProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null){
            logger.error(SECURITY_MARKER,"User not found");
            throw new NotFoundException("User not found");
        }

        if (user.getAdmin() == null){
            logger.error(SECURITY_MARKER, "Admin data not found for user " + username);
            throw new NotFoundException("Admin data not found for user: " + username);
        }

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
