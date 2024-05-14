package com.bni.finproajubackend.model.user.nasabah;

import com.bni.finproajubackend.model.ticket.Tickets;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
