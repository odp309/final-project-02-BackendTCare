package com.bni.finproajubackend.model.user.nasabah;

import com.bni.finproajubackend.model.enumobject.Gender;
import com.bni.finproajubackend.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(unique = true)
    private String nik;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
    @Column(unique = true)
    private String email;
    @Column(name = "phone_number", unique = true)
    private String noHP;
    @Column(name = "first_name")
    private String first_name;
    @Column(name = "last_name")
    private String last_name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int age;
    private String address;
    @OneToMany(mappedBy = "nasabah")
    private List<Account> account;
}
