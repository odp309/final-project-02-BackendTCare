package com.bni.finproajubackend.model.user.nasabah;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "detail_rekening")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "nasabah_id", referencedColumnName = "id")
    private Nasabah nasabah;
    private String type;
    @Column(unique = true)
    private String account_number;
    private String balance;
    @OneToMany(mappedBy = "account")
    private List<Transaction> transaction;
}
