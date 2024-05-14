package com.bni.finproajubackend.model.user.nasabah;

import com.bni.finproajubackend.model.user.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "admin")
public class Nasabah {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;
    @Column(unique = true)
    private String nik;
    @OneToMany(mappedBy = "nasabah")
    private List<Account> account;
}
