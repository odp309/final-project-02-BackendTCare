package com.bni.finproajubackend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    public String getClientIp() {
        String remoteAddr = "";

        if (httpServletRequest != null) {
            remoteAddr = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = httpServletRequest.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}
