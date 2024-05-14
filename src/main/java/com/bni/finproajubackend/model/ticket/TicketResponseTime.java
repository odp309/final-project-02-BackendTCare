package com.bni.finproajubackend.model.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_response_time")
public class TicketResponseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Tickets ticket;
}
