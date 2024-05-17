package com.bni.finproajubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
@Entity
@Table(name = "token_revocation_list")
public class TokenRevocation {

    @Id
    private String token;
    private Date expirationTime;
}