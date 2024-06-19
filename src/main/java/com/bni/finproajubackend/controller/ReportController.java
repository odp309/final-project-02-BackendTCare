package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.interfaces.ReportInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/private/admin/reports-stats")
public class ReportController {

    @Autowired
    private ReportInterface reportService;
    @Autowired
    private TemplateResInterface responseService;

    @GetMapping("/{path}")
    public ResponseEntity getStatus(
            @PathVariable String path,
            @RequestParam Map<String, String> queryParams) {
        try {
            ReportResponseDTO result = reportService.getReports(queryParams, path);
            return ResponseEntity.ok(responseService.apiSuccessReport(result, "Data acquired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Failed Getting Data" : e.getMessage()));
        }
    }
}
