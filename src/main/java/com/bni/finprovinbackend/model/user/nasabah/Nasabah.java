package com.bni.finprovinbackend.model.user.nasabah;

import com.bni.finprovinbackend.model.user.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "nasabah")
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
