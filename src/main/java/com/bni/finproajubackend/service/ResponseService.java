package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.PaginationDTO;
import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplatePaginationResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService implements TemplateResInterface {

    @Override
    public <T> TemplateResponseDTO<T> apiSuccess(T data, String message) {
        return createResponse(data, HttpStatus.OK, "Success");
    }

    @Override
    public <T> TemplatePaginationResponseDTO<T> apiSuccessPagination(PaginationDTO data, String message) {
        TemplatePaginationResponseDTO<T> templatePaginationResponseDTO = new TemplatePaginationResponseDTO<>();
        templatePaginationResponseDTO.setStatusCode(HttpStatus.OK);
        templatePaginationResponseDTO.setMessage(message);
        if (data == null) {
            templatePaginationResponseDTO.setResult(null);
            templatePaginationResponseDTO.setCurrent_page(1);
            templatePaginationResponseDTO.setCurrent_item(0);
            templatePaginationResponseDTO.setTotal_page(1);
            templatePaginationResponseDTO.setTotal_item(0);
            return templatePaginationResponseDTO;
        }
        templatePaginationResponseDTO.setResult((T) data.getData());
        templatePaginationResponseDTO.setCurrent_page(data.getCurrentPage() == 0 ? 1 : data.getCurrentPage());
        templatePaginationResponseDTO.setCurrent_item(data.getCurrentItem());
        templatePaginationResponseDTO.setTotal_page(data.getTotalPage());
        templatePaginationResponseDTO.setTotal_item(data.getTotalItem());
        return templatePaginationResponseDTO;
    }

    @Override
    public <T> TemplateReportResponseDTO<T> apiSuccessReport(ReportResponseDTO data, String message) {
        Map<String, Long> reportDetails = getStringLongMap(data);

//        TemplateReportResponseDTO<T> templateReportResponseDTO = new TemplateReportResponseDTO<>();
//        templateReportResponseDTO.setMessage(message);
//        templateReportResponseDTO.setStatusCode(HttpStatus.OK);
//        templateReportResponseDTO.setResult((T) reportDetails);
//        templateReportResponseDTO.setTotal(data.getTotal());
//        templateReportResponseDTO.setTotal_all_year(data.getTotalAllReports());
//        templateReportResponseDTO.setYear(data.getYear());

        return null;
    }

    private @NotNull Map<String, Long> getStringLongMap(ReportResponseDTO data) {
        Map<String, Long> reportDetails = new HashMap<>();

        return null;
    }

    @Override
    public <T> TemplateResponseDTO<T> apiNotFound(T data, String message) {
        return createResponse(data, HttpStatus.NOT_FOUND, message);
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
