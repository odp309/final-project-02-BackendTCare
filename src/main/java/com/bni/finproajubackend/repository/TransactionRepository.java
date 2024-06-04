package com.bni.finproajubackend.repository;

//import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
}
