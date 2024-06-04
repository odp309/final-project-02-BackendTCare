package com.bni.finproajubackend.model.ticket;

import com.bni.finproajubackend.model.enumobject.StarRating;
import com.bni.finproajubackend.model.user.admin.Admin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ticket_feedback")
public class TicketFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Tickets ticket;
    @ManyToOne
    @Column(name = "star_rating")
    private StarRating starRating;
    @ManyToOne
    @Column(name = "comment")
    private Long comment;

}
