package com.bni.finproajubackend.model.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "ticket_categories")
public class TicketCategories {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "category_name")
    private String categoryName;
    @Column(name = "category_description")
    private String categoryDescription;
    @OneToMany(mappedBy = "ticketCategory")
    private List<Tickets> tickets;
}
