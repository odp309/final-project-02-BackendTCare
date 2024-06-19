package com.bni.finproajubackend.dto.tickets;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class TrackTicketStatusResponseDTO {
    private String pic;
    private String date;
    private String category;
    private String description;
}
