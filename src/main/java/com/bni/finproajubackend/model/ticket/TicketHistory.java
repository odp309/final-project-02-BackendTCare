package com.bni.finproajubackend.model.ticket;

import com.bni.finproajubackend.model.user.admin.Admin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ticket_history")
public class TicketHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Tickets ticket;
    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id")
    private Admin admin;
    @Column(name = "ticket_history_description")
    private String description;
    @Column(name = "ticket_history_date")
    private Date date;
    @Column(name = "level")
    private Long level;
}
