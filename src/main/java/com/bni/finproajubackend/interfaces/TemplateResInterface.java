package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;

public interface TemplateResInterface {
    <T> TemplateResponseDTO<T> apiSuccess(T data);

    <T> TemplateResponseDTO<T> apiFailed(T data);

    <T> TemplateResponseDTO<T> apiBadRequest(T data);

    <T> TemplateResponseDTO<T> apiUnauthorized(T data);
}
