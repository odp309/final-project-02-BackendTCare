package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import com.bni.finproajubackend.interfaces.AdminInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements AdminInterface {

    @Autowired
    private AdminRepository adminRespository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AdminResponseDTO getAdminProfile(Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName());
        return AdminResponseDTO.builder()
                .person(admin.getPerson())
                .role(admin.getPerson().getAdmin().getRole())
                .npp(admin.getPerson().getAdmin().getNpp())
                .build();
    }
}
