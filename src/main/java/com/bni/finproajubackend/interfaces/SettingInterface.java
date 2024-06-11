package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.setting.SettingDTO;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import org.springframework.security.core.Authentication;

public interface SettingInterface {
    SettingDTO getNasabahName (Authentication authentication);
}
