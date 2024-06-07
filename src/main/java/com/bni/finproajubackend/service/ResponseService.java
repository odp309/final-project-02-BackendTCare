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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService implements TemplateResInterface {

    @Override
    public <T> TemplateResponseDTO<T> apiSuccess(T data, String message) {
        return createResponse(data, HttpStatus.OK, "Success");
    }

    @Override
    public <T>TemplatePaginationResponseDTO<T> apiSuccessPagination(PaginationDTO data, String message) {
        TemplatePaginationResponseDTO<T> templatePaginationResponseDTO = new TemplatePaginationResponseDTO<>();
        templatePaginationResponseDTO.setStatusCode(HttpStatus.OK);
        templatePaginationResponseDTO.setMessage(message);
        if(data == null){
            templatePaginationResponseDTO.setCurrentPage(0);
            templatePaginationResponseDTO.setCurrentItem(0);
            templatePaginationResponseDTO.setTotalPage(0);
            templatePaginationResponseDTO.setTotalItem(0);
            return templatePaginationResponseDTO;
        }
        templatePaginationResponseDTO.setResult(data.getData());
        templatePaginationResponseDTO.setCurrentPage(data.getCurrentPage());
        templatePaginationResponseDTO.setCurrentItem(data.getCurrentItem());
        templatePaginationResponseDTO.setTotalPage(data.getTotalPage());
        templatePaginationResponseDTO.setTotalItem(data.getTotalItem());
        return templatePaginationResponseDTO;
    }

    @Override
    public <T> TemplateReportResponseDTO<T> apiSuccessReport(ReportResponseDTO data, String message) {
        Map<String, Long> reportDetails = getStringLongMap(data);

        TemplateReportResponseDTO<T> templateReportResponseDTO = new TemplateReportResponseDTO<>();
        templateReportResponseDTO.setMessage(message);
        templateReportResponseDTO.setStatusCode(HttpStatus.OK);
        templateReportResponseDTO.setResult((T) reportDetails);
        templateReportResponseDTO.setTotal(data.getTotal());
        templateReportResponseDTO.setTotal_all_year(data.getTotalAllReports());
        templateReportResponseDTO.setYear(data.getYear());

        return templateReportResponseDTO;
    }

    private @NotNull Map<String, Long> getStringLongMap(ReportResponseDTO data) {
        Map<String, Long> reportDetails = new HashMap<>();
        reportDetails.put("january", data.getJanuary());
        reportDetails.put("february", data.getFebruary());
        reportDetails.put("march", data.getMarch());
        reportDetails.put("april", data.getApril());
        reportDetails.put("may", data.getMay());
        reportDetails.put("june", data.getJune());
        reportDetails.put("july", data.getJuly());
        reportDetails.put("august", data.getAugust());
        reportDetails.put("september", data.getSeptember());
        reportDetails.put("october", data.getOctober());
        reportDetails.put("november", data.getNovember());
        reportDetails.put("december", data.getDecember());
        return reportDetails;
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
