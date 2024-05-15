package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import org.springframework.security.core.Authentication;

public interface AdminInterface {
    AdminResponseDTO getAdminProfile(Authentication authentication);
}
