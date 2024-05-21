package com.bni.finprovinbackend.model.user.admin;

import com.bni.finprovinbackend.model.ticket.TicketHistory;
import com.bni.finprovinbackend.model.user.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
    @Column(unique = true)
    private String npp;
    @OneToMany(mappedBy = "admin")
    private List<TicketHistory> ticketHistory;
}
