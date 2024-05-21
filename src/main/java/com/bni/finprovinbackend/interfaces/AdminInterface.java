package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.admin.AdminResponseDTO;
import org.springframework.security.core.Authentication;

public interface AdminInterface {
    AdminResponseDTO getAdminProfile(Authentication authentication);
}
