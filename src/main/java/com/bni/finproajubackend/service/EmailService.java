package com.bni.finproajubackend.service;

import com.bni.finproajubackend.aspect.PermissionAspect;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final Marker EMAIL_MARKER = MarkerFactory.getMarker("EMAIL");
    @Autowired
    private LoggerService loggerService;

    @Async
    public void sendNotification(Tickets ticket) throws MessagingException {
        String to = ticket.getTransaction().getAccount().getNasabah().getEmail();
        String subject;
        String message;

        if (ticket.getTicketStatus() == TicketStatus.Selesai) {
            subject = "Tiket Anda Telah Selesai";
            message = createCompletedTicketMessage(ticket);
        } else {
            subject = "Pembaruan Status Tiket";
            message = createUpdatedTicketMessage(ticket);
        }

        sendEmail(to, subject, message);
    }

    private String createCompletedTicketMessage(Tickets ticket) {
        try {
            String recipient = ticket.getTransaction().getAccount().getNasabah().getFirst_name();
            String ticketId = ticket.getTicketNumber();
            LocalDateTime transactionDate = ticket.getTransaction().getCreatedAt();
            Long amount = ticket.getTransaction().getAmount();
            TicketStatus resolutionStatus = ticket.getTicketStatus();
            String description = ticket.getDescription();
            String division = "PT Bank Negara Indonesia (Persero) Tbk.";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
            String formattedDateTime = transactionDate.format(formatter);

            logger.info(EMAIL_MARKER, "IP {} Sending complete email to {} with subject {}", loggerService.getClientIp(), ticket.getTransaction().getAccount().getNasabah().getEmail(), "Pembaruan Status Tiket");

            return String.format(
                    "<html><body>" +
                            "Yth. %s,<br><br>" +
                            "Kami ingin memberitahukan bahwa status dari masalah yang Anda laporkan telah diperbarui.<br><br>" +
                            "ID Tiket: <b>%s</b><br>" +
                            "Tanggal Transaksi: %s<br>" +
                            "Jumlah: <b>Rp.%s</b><br>" +
                            "Deskripsi: %s<br>" +
                            "Status: <b>%s</b><br><br>" +
                            "Anda memiliki waktu 3 hari setelah laporan selesai untuk memberikan penilaian dan mengajukan pengaduan ulang jika masalah yang Anda alami belum benar-benar terselesaikan dengan baik.<br><br>" +
                            "Terima kasih atas kesabaran dan kerja sama Anda selama proses pengaduan ini.<br><br>" +
                            "Hormat kami,<br><br>" +
                            "<b>%s</b>" +
                            "</body></html>",
                    recipient, ticketId, formattedDateTime, formatAmount(amount), description, resolutionStatus, division
            );

        } catch (RuntimeException e) {
            logger.error(EMAIL_MARKER, "Error creating email message", e);
            throw new RuntimeException("Email Failed");
        }
    }

    private String createUpdatedTicketMessage(Tickets ticket) {
        try {
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

            logger.info(EMAIL_MARKER, "IP {} Sending update email to {} with subject {}", loggerService.getClientIp(), ticket.getTransaction().getAccount().getNasabah().getEmail(), "Pembaruan Status Tiket");

            return String.format(
                    "<html><body>" +
                            "Yth. %s,<br><br>" +
                            "Kami ingin memberitahukan bahwa status dari masalah yang Anda laporkan telah diperbarui.<br><br>" +
                            "ID Tiket: <b>%s</b><br>" +
                            "Tanggal Transaksi: %s<br>" +
                            "Jumlah: <b>Rp.%s</b><br>" +
                            "Deskripsi: %s<br>" +
                            "Status: <b>%s</b><br><br>" +
                            "Kami telah menerima laporan Anda dan saat ini sedang dalam proses penanganan. Kami akan memberikan pembaruan lebih lanjut segera setelah ada perkembangan.<br><br>" +
                            "Kami mengingatkan bahwa SLA (Service Level Agreement) untuk penyelesaian tiket adalah <b>10 hari kerja</b>. Kami akan berupaya keras untuk menyelesaikan masalah ini dalam batas waktu tersebut.<br><br>" +
                            "Mohon bersabar hingga status ticketnya selesai. Terima kasih atas pengertian dan kerjasamanya.<br><br>" +
                            "Hormat kami,<br><br>" +
                            "<b>%s</b>" +
                            "</body></html>",
                    recipient, ticketId, formattedDateTime, formatAmount(amount), description, formattedResolutionStatus, division
            );
        } catch (RuntimeException e) {
            logger.error(EMAIL_MARKER, "Error creating email message", e);
            throw new RuntimeException("Email Failed");
        }
    }

    private void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // set the second parameter to 'true' to indicate HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error(EMAIL_MARKER, "Error sending email", e);
            throw new MessagingException("Failed sending email"); // handle error appropriately
        }
    }

    private String formatTicketStatus(TicketStatus status) {
        switch (status) {
            case DalamProses:
                return "Dalam Proses";
            case Selesai:
                return "Selesai";
            // Tambahkan case lain jika ada
            default:
                return status.toString();
        }
    }

    private String formatAmount(Long amount) {
        return String.format("%,d", amount).replace(",", ".");
    }
}
