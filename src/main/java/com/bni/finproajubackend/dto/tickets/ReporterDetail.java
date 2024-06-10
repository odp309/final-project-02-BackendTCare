package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporterDetail {
    private String nama;
    private String account_number;
    private String address;
    private String no_handphone;
}
