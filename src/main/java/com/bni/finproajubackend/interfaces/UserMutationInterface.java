package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import org.springframework.security.core.Authentication;

public interface UserMutationInterface {
    UserMutationDTO getUserMutations(Authentication authentication);
}
