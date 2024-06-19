package com.bni.finproajubackend.model.user.nasabah;

import com.bni.finproajubackend.model.bank.Bank;
import com.bni.finproajubackend.model.enumobject.TransactionCategories;
import com.bni.finproajubackend.model.enumobject.TransactionType;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private Long referenced_id;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "rekening_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "transaction_detail")
    private String detail;

    @Column(name = "transaction_status")
    private String status;

    private Long amount;

    private Long total_amount;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transaction_type;

    @ManyToOne
    @JoinColumn(name = "recipient_bank_id", referencedColumnName = "id")
    private Bank recipient_bank;

    @Column(name = "category") // Menambahkan properti untuk enum
    @Enumerated(EnumType.STRING) // Menyimpan nilai enum sebagai string dalam database
    private TransactionCategories category; // Properti yang menggunakan enum

    @ManyToOne
    @JoinColumn(name = "recipient_rekening_id", referencedColumnName = "id")
    private Account recipient_account;

    @OneToMany(mappedBy = "transaction")
    @OrderBy("createdAt DESC")
    @JsonIgnore
    private List<Tickets> tickets;
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
