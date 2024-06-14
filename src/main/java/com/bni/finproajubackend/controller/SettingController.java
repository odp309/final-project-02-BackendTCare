package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.setting.SettingDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/private")
public class SettingController {
    @Autowired
    private TemplateResInterface responseService;

    @Autowired
    private SettingService settingService;

    private Map<String, Object> errorDetails = new HashMap<>();

    @GetMapping(value = "/customer/setting",produces = "application/json")
    public ResponseEntity setting(Authentication authentication){
        try{
            SettingDTO settingDTO = settingService.getNasabahName(authentication);
            return ResponseEntity.ok(responseService.apiSuccess(settingDTO, "Success"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiNotFound(null, e.getCause()==null ? "Something went wrong" : e.getMessage()));
        } catch (Exception e){
            errorDetails.put("message", e.getCause()== null ? "Not Permitted": e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.apiFailed(null, e.getCause()==null ? "Something went wrong" : e.getMessage()));
        }
    }

}
