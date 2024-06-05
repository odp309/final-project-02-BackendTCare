package com.bni.finproajubackend.model.ticket;

import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class Tickets {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String ticketNumber;
    @OneToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;
    @Column(name = "ticket_category")
    private TicketCategories ticketCategory;
    @Column(name = "ticket_status")
    private TicketStatus ticketStatus;
    @Column(name = "ticket_description")
    private String description;
    @Column(name = "date")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TicketFeedback> ticketFeedbacks;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TicketHistory> ticketHistory;
    @OneToOne(mappedBy = "ticket")
    private TicketResponseTime ticketResponseTime;
}
