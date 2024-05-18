package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class ResponseService implements TemplateResInterface {

    @Override
    public <T> TemplateResponseDTO<T> apiSuccess(T data) {
        return createResponse(data, HttpStatus.OK, "Success");
    }

    @Override
    public <T> TemplateResponseDTO<T> apiFailed(T data) {
        return createResponse(data, HttpStatus.INTERNAL_SERVER_ERROR, "Failed");
    }

    @Override
    public <T> TemplateResponseDTO<T> apiBadRequest(T data) {
        return createResponse(data, HttpStatus.BAD_REQUEST, "Request Invalid");
    }

    @Override
    public <T> TemplateResponseDTO<T> apiUnauthorized(T data) {
        return createResponse(data, HttpStatus.UNAUTHORIZED, "User is not Authorized for this API");
    }

    private <T> TemplateResponseDTO<T> createResponse(T data, HttpStatus status, String message) {
        TemplateResponseDTO<T> response = new TemplateResponseDTO<>();
        response.setResult(data);
        response.setStatusCode(status);
        response.setMessage(message);
        response.setType(MediaType.APPLICATION_JSON_VALUE);
        return response;
    }

}
