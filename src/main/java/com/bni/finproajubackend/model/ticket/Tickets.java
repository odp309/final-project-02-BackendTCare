package com.bni.finproajubackend.model.ticket;

//import com.bni.finproajubackend.listener.TicketListener;
import com.bni.finproajubackend.model.enumobject.DivisiTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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
    @Column(name = "ticket_category")
    @Enumerated(EnumType.STRING)
    private TicketCategories ticketCategory;
    @Column(name = "ticket_status")
    private TicketStatus ticketStatus;
    @Column(name = "divisi_target")
    private DivisiTarget divisiTarget;
    @Column(name = "ticket_description")
    private String description;
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL)
    @JsonIgnore
    private TicketFeedback ticketFeedbacks;
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TicketHistory> ticketHistory;
    @OneToOne(mappedBy = "ticket")
    private TicketResponseTime ticketResponseTime;
}
