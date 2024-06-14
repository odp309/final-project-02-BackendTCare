package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplatePaginationResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;

public interface TemplateResInterface {
    <T> TemplateResponseDTO<T> apiSuccess(T data, String message);

    <T>TemplatePaginationResponseDTO<T> apiSuccessPagination(PaginationDTO data, String message);

    <T> TemplateReportResponseDTO<T> apiSuccessReport(ReportResponseDTO data, String message);

    <T> TemplateResponseDTO<T> apiNotFound(T data, String message);

    <T> TemplateResponseDTO<T> apiFailed(T data, String message);

    <T> TemplateResponseDTO<T> apiBadRequest(T data, String message);

    <T> TemplateResponseDTO<T> apiUnauthorized(T data, String message);
}
