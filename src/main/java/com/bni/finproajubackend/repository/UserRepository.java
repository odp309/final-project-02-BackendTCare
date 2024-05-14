package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Transactional
    Integer deleteByUsername(String username);
    boolean existsByUsername(String username);
}
