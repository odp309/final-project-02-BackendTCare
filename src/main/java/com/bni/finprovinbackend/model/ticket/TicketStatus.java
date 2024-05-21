package com.bni.finprovinbackend.model.ticket;

import com.bni.finprovinbackend.model.enumobject.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ticket_status")
public class TicketStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "status_name")
    private Status status;
    @OneToMany(mappedBy = "ticketStatus")
    private List<Tickets> tickets;
}
