package com.bni.finproajubackend.model.user;

import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Admin admin;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Nasabah nasabah;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
}
