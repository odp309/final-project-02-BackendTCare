package com.bni.finproajubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "token_revocation_list")
public class TokenRevocation {

    @Id
    private String token;
    private LocalDateTime expirationTime;
}