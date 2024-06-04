package com.bni.finproajubackend.service;

import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(Tickets ticket) {
        String to = ticket.getTransaction().getAccount().getNasabah().getEmail();
        String subject = "Ticket Status Update";

        String recipient = ticket.getTransaction().getAccount().getNasabah().getFirstName();
        String ticketId = ticket.getTicketNumber();
        LocalDateTime transactionDate = ticket.getTransaction().getCreatedAt();
        Long amount = ticket.getTransaction().getAmount();
        String description = ticket.getDescription();
        TicketStatus resolutionStatus = ticket.getTicketStatus();
        String division = "PT Bank Negara Indonesia (Persero) Tbk.";


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");
        String formattedDateTime = transactionDate.format(formatter);

        String message = String.format(
                "Dear %s,%n%n" +
                        "We would like to inform you that the status of your reported issue has been updated.%n%n" +
                        "Ticket ID: %s%n" +
                        "Transaction Date: %s%n" +
                        "Amount: %s%n" +
                        "Description: %s%n" +
                        "Resolution Status: %s%n%n" +
                        "Thank you for your patience and cooperation throughout the resolution process.%n%n" +
                        "Sincerely,%n" +
                        "%s",
                recipient, ticketId, formattedDateTime, amount, description, resolutionStatus, division
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
}
