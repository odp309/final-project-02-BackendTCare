package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByNasabah(Nasabah nasabah);

    Account findByAccountNumber(String accountNumber);
}
