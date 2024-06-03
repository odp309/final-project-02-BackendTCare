package com.bni.finproajubackend.model.user.nasabah;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JoinColumn(name = "nasabah_id", referencedColumnName = "id")
    private Nasabah nasabah;
    private String type;
    @Column(unique = true)
    private String account_number;
    private Long balance;
    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Transaction> transaction;
}
