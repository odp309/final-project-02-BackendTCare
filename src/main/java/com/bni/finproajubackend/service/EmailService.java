package com.bni.finproajubackend.service;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendNotification(Tickets ticket) {
        String to = ticket.getTransaction().getAccount().getNasabah().getEmail();
        String subject = "Pembaruan Status Tiket";

        String recipient = ticket.getTransaction().getAccount().getNasabah().getFirst_name();
        String ticketId = ticket.getTicketNumber();
        LocalDateTime transactionDate = ticket.getTransaction().getCreatedAt();
        Long amount = ticket.getTransaction().getAmount();
        String description = ticket.getDescription();
        TicketStatus resolutionStatus = ticket.getTicketStatus();
        String division = "PT Bank Negara Indonesia (Persero) Tbk.";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
        String formattedDateTime = transactionDate.format(formatter);

        String formattedResolutionStatus = formatTicketStatus(resolutionStatus);

        String message = String.format(
                "Yth. %s,%n%n" +
                        "Kami ingin memberitahukan bahwa status dari masalah yang Anda laporkan telah diperbarui.%n%n" +
                        "ID Tiket: %s%n" +
                        "Tanggal Transaksi: %s%n" +
                        "Jumlah: %s%n" +
                        "Deskripsi: %s%n" +
                        "Status Resolusi: %s%n%n" +
                        "Terima kasih atas kesabaran dan kerja sama Anda selama proses penyelesaian ini.%n%n" +
                        "Hormat kami,%n" +
                        "%s",
                recipient, ticketId, formattedDateTime, amount, description, formattedResolutionStatus, division
        );
        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private String formatTicketStatus(TicketStatus status) {
        switch (status) {
            case DalamProses:
                return "Dalam Proses";
            // Tambahkan case lain jika ada
            default:
                return status.toString();
        }
    }
}