package com.bni.finproajubackend.model.user.nasabah;

import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "rekening_id", referencedColumnName = "id")
    private Account account;
    @Column(name = "transaction_detail")
    private String detail;
    @Column(name = "transaction_status")
    private String status;
    @OneToOne(mappedBy = "transaction")
    private Tickets tickets;
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
