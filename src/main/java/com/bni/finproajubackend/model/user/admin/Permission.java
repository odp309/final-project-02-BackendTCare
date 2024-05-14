package com.bni.finproajubackend.model.user.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;
    @Column(name = "permission_description")
    private String permissionDescription;
    @ManyToMany(mappedBy = "permission")
    private List<Role> roles;
}
