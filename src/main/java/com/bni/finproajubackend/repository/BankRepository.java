package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.bank.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
