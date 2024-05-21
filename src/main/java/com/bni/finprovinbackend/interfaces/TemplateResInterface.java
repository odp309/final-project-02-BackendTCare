package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.templateResponse.TemplateResponseDTO;

public interface TemplateResInterface {
    <T> TemplateResponseDTO<T> apiSuccess(T data);

    <T> TemplateResponseDTO<T> apiFailed(T data);

    <T> TemplateResponseDTO<T> apiBadRequest(T data);

    <T> TemplateResponseDTO<T> apiUnauthorized(T data);
}
