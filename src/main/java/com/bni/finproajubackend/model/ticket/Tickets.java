package com.bni.finproajubackend.model.ticket;

import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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
    @ManyToOne
    @JoinColumn(name = "ticket_category_id", referencedColumnName = "id")
    private TicketCategories ticketCategory;
    @ManyToOne
    @JoinColumn(name = "ticket_status_id", referencedColumnName = "id")
    private TicketStatus ticketStatus;
    @Column(name = "ticket_description")
    private String description;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TicketHistory> ticketHistory;
    @OneToOne(mappedBy = "ticket")
    private TicketResponseTime ticketResponseTime;
}
