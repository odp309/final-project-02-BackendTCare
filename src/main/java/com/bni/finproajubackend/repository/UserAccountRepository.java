package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<User, Long> {
}
