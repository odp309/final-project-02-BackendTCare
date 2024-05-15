package com.bni.finproajubackend.model.user;

import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.enumobject.Gender;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(unique = true)
    private String email;
    @Column(name = "phone_number", unique = true)
    private String noHP;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int age;
    private String address;

    @OneToOne(mappedBy = "person")
    private Admin admin;
    @OneToOne(mappedBy = "person")
    private Nasabah nasabah;
}
