package com.bni.finprovinbackend.model.ticket;

import com.bni.finprovinbackend.model.user.nasabah.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class Tickets {
    @Id
    private UUID id;
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

    @OneToOne(mappedBy = "ticket")
    private TicketResponseTime ticketResponseTime;
}
