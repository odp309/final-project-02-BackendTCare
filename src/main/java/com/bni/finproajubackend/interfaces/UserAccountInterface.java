package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.userAccount.UserAccountDTO;
import org.springframework.security.core.Authentication;

public interface UserAccountInterface {
    UserAccountDTO getUserAccount(Authentication authentication);
}
