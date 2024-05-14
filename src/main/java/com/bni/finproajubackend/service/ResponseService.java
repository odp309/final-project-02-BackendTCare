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
        TemplateResponseDTO<T> response = new TemplateResponseDTO<>();
        response.setResult(data);
        response.setStatusCode(HttpStatus.OK);
        response.setMessage("Success");
        response.setType(MediaType.APPLICATION_JSON_VALUE);
        return response;
    }

    @Override
    public <T> TemplateResponseDTO<T> apiFailed(T data) {
        TemplateResponseDTO<T> response = new TemplateResponseDTO<>();
        response.setResult(data);
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.setMessage("Failed");
        response.setType(MediaType.APPLICATION_JSON_VALUE);
        return response;
    }

    @Override
    public <T> TemplateResponseDTO<T> apiBadRequest(T data) {
        TemplateResponseDTO<T> response = new TemplateResponseDTO<>();
        response.setResult(data);
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.setMessage("Request Invalid");
        response.setType(MediaType.APPLICATION_JSON_VALUE);
        return response;
    }
}
