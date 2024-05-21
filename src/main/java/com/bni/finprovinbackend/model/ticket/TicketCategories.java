package com.bni.finprovinbackend.model.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "ticket_categories")
public class TicketCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "category_name")
    private String name;
    @OneToMany(mappedBy = "ticketCategory")
    private List<Tickets> tickets;
}
