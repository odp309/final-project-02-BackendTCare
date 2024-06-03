package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ResponseService implements TemplateResInterface {

    @Override
    public <T> TemplateResponseDTO<T> apiSuccess(T data, String message) {
        return createResponse(data, HttpStatus.OK, "Success");
    }

    @Override
    public <T> TemplateResponseDTO<T> apiFailed(T data, String message) {
        return createResponse(data, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @Override
    public <T> TemplateResponseDTO<T> apiBadRequest(T data, String message) {
        return createResponse(data, HttpStatus.BAD_REQUEST, message);
    }

    @Override
    public <T> TemplateResponseDTO<T> apiUnauthorized(T data, String message) {
        if (message.equalsIgnoreCase("User not Permitted")) {
            return createResponse(data, HttpStatus.FORBIDDEN, message);
        } else {
            return createResponse(data, HttpStatus.UNAUTHORIZED, message);
        }
    }

    private <T> TemplateResponseDTO<T> createResponse(T data, HttpStatus status, String message) {
        TemplateResponseDTO<T> response = new TemplateResponseDTO<>();
        response.setResult(data);
        response.setStatusCode(status);
        response.setMessage(message);
        return response;
    }

}
