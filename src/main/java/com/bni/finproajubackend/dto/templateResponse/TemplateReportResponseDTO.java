package com.bni.finproajubackend.dto.templateResponse;

import lombok.Builder;
import lombok.Data;

@Data
public class TemplateReportResponseDTO<T> extends ReportResponseDTO {
    private T result;
}
