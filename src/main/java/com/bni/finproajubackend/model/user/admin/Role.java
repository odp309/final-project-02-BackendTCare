package com.bni.finproajubackend.model.user.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    @Column(name = "division_name", nullable = false, unique = true)
    private String divisionName;
    @Column(name = "role_description")
    private String roleDescription;
}
