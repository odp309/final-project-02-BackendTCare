package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.templateResponse.TemplateResponseDTO;

public interface TemplateResInterface {
    <T> TemplateResponseDTO<T> apiSuccess(T data, String message);

    <T> TemplateResponseDTO<T> apiFailed(T data, String message);

    <T> TemplateResponseDTO<T> apiBadRequest(T data, String message);

    <T> TemplateResponseDTO<T> apiUnauthorized(T data, String message);
}
