package com.bni.finproajubackend.service;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.ticket.TicketFeedback;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketFeedbackRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmartTicketService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TicketFeedbackRepository ticketFeedbackRepository;
    @Autowired
    private TicketsRepository ticketsRepository;

    @Async
    public void updateTicketAdmin(Tickets tickets) {
        List<Admin> admins = getListAdminByDivision(switch (tickets.getTicketCategory()) {
            case Transfer -> DivisionTarget.WPP;
            case TopUp -> DivisionTarget.DGO;
            case Payment -> DivisionTarget.CXC;
            default -> throw new IllegalStateException("Unexpected value: " + tickets.getTicketCategory().name());
        });

        Admin assignedAdmin = admins.stream()
                .min(Comparator.comparingInt(Admin::getQuota))
                .orElseThrow(() -> new RuntimeException("No admin available"));

        tickets.setAdmin(assignedAdmin);
        assignedAdmin.setQuota(assignedAdmin.getQuota() + 1);

        adminRepository.save(assignedAdmin);
        ticketsRepository.save(tickets);
    }

    private List<Admin> getListAdminByDivision(DivisionTarget divisionTarget) {
        List<Admin> admins = adminRepository.findAllByDivisionTarget(divisionTarget);
        return admins.stream()
                .sorted((a1, a2) -> {
                    double avgRating1 = getAverageRating(a1);
                    double avgRating2 = getAverageRating(a2);
                    return Double.compare(avgRating2, avgRating1);
                })
                .collect(Collectors.toList());
    }

    private double getAverageRating(Admin admin) {
        List<TicketFeedback> feedbacks = ticketFeedbackRepository.findAllByTicket_Admin(admin);
        return feedbacks.stream()
                .mapToInt(feedback -> feedback.getStar_rating().getValue())
                .average()
                .orElse(0.0);
    }
}
