package com.bni.finproajubackend.model.bank;

import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "bank_name")
    private String bankName;

    @OneToMany(mappedBy = "bank")
    @JsonIgnore
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "recipient_bank")
    @JsonIgnore
    private List<Transaction> recipient_transactions;
}
