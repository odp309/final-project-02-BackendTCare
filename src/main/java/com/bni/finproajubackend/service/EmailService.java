package com.bni.finproajubackend.service;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

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
        logger.info(EMAIL_MARKER, "sendNotification called with ticket: {}", ticket);

        if (ticket == null) {
            logger.error(EMAIL_MARKER, "Ticket is null");
            throw new IllegalArgumentException("Ticket cannot be null");
        }

        if (mailSender == null) {
            logger.error(EMAIL_MARKER, "JavaMailSender is not initialized");
            throw new IllegalStateException("JavaMailSender is not initialized");
        }

        if (loggerService == null) {
            logger.error(EMAIL_MARKER, "LoggerService is not initialized");
            throw new IllegalStateException("LoggerService is not initialized");
        }

        String to = Optional.ofNullable(ticket.getTransaction())
                .map(t -> t.getAccount().getNasabah().getEmail())
                .orElseThrow(() -> {
                    logger.error(EMAIL_MARKER, "Email address is null for ticket: {}", ticket);
                    return new IllegalArgumentException("Email address cannot be null");
                });

        logger.info(EMAIL_MARKER, "Sending email to: {}", to);

        String subject;
        String message;

        try {
            if (ticket.getTicketStatus() == TicketStatus.Selesai) {
                subject = "Tiket Anda Telah Selesai";
                message = createCompletedTicketMessage(ticket);
            } else {
                subject = "Pembaruan Status Tiket";
                message = createUpdatedTicketMessage(ticket);
            }
            sendEmail(to, subject, message);
        } catch (Exception e) {
            logger.error(EMAIL_MARKER, "Error occurred while preparing to send email", e);
            throw new MessagingException("Failed to send email", e);
        }

        logger.info(EMAIL_MARKER, "Email sent successfully to: {}", to);
    }

    private String createCompletedTicketMessage(Tickets ticket) {
        try {
            String recipient = Optional.ofNullable(ticket.getTransaction())
                    .map(t -> t.getAccount().getNasabah().getFirst_name())
                    .orElse("Nasabah");
            String ticketId = ticket.getTicketNumber();
            LocalDateTime transactionDate = ticket.getTransaction().getCreatedAt();
            Long amount = ticket.getTransaction().getAmount();
            TicketStatus resolutionStatus = ticket.getTicketStatus();
            String description = ticket.getDescription();
            String division = "PT Bank Negara Indonesia (Persero) Tbk.";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
            String formattedDateTime = transactionDate.format(formatter);

            logger.info(EMAIL_MARKER, "Creating completed ticket email for ticket: {}", ticket);

            return String.format(
                    "<html><body>" +
                            "Yth. %s,<br><br>" +
                            "Kami ingin memberitahukan bahwa status dari masalah yang Anda laporkan telah diperbarui.<br><br>" +
                            "ID Tiket: <b>%s</b><br>" +
                            "Tanggal Transaksi: %s<br>" +
                            "Jumlah: <b>Rp.%s</b><br>" +
                            "Deskripsi: %s<br>" +
                            "Status: <b>%s</b><br><br>" +
                            "Anda memiliki waktu 3 hari setelah laporan selesai untuk memberikan penilaian dan mengajukan pengaduan ulang jika masalah yang Anda alami belum benar-benar terselesaikan. Pengaduan ulang dapat dilakukan sendiri melalui fitur T-Care dengan melihat detail tiket dan mengajukan pengaduan ulang.<br><br>" +
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
            String recipient = Optional.ofNullable(ticket.getTransaction())
                    .map(t -> t.getAccount().getNasabah().getFirst_name())
                    .orElse("Nasabah");
            String ticketId = ticket.getTicketNumber();
            LocalDateTime transactionDate = ticket.getTransaction().getCreatedAt();
            Long amount = ticket.getTransaction().getAmount();
            String description = ticket.getDescription();
            TicketStatus resolutionStatus = ticket.getTicketStatus();
            String division = "PT Bank Negara Indonesia (Persero) Tbk.";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
            String formattedDateTime = transactionDate.format(formatter);

            String formattedResolutionStatus = formatTicketStatus(resolutionStatus);

            logger.info(EMAIL_MARKER, "Creating updated ticket email for ticket: {}", ticket);

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
            logger.info(EMAIL_MARKER, "Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error(EMAIL_MARKER, "Error sending email to: {}", to, e);
            throw new MessagingException("Failed sending email", e); // handle error appropriately
        }
    }

    private String formatTicketStatus(TicketStatus status) {
        switch (status) {
            case DalamProses:
                return "Dalam Proses";
            case Selesai:
                return "Selesai";
            default:
                return status.toString();
        }
    }

    private String formatAmount(Long amount) {
        return String.format("%,d", amount).replace(",", ".");
    }
}
