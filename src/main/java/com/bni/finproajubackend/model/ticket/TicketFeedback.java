package com.bni.finproajubackend.model.ticket;

import com.bni.finproajubackend.model.enumobject.StarRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Column(name = "star_rating")
    private StarRating starRating;
    @Column(name = "comment")
    private String comment;

}
