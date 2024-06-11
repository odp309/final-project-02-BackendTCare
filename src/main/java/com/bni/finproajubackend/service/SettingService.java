package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.setting.SettingDTO;
import com.bni.finproajubackend.interfaces.SettingInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SettingService implements SettingInterface {
    @Autowired
    private UserRepository userRepository;
    @Override
    public SettingDTO getNasabahName(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setName(user.getNasabah().getFirst_name());

        return settingDTO;
    }
}
