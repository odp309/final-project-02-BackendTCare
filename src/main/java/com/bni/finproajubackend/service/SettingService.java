package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.dto.setting.SettingDTO;
import com.bni.finproajubackend.interfaces.SettingInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.webjars.NotFoundException;

@Service
public class SettingService implements SettingInterface {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    private static final Marker NASABAH_MARKER = MarkerFactory.getMarker("NASABAH");

    @Override
    public SettingDTO getNasabahName(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new NotFoundException("User " + username + "not found");

        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setName(user.getNasabah().getFirst_name());

        logger.info(NASABAH_MARKER, "Settings acquired");

        return settingDTO;
    }
}
