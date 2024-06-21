package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Override
    Optional<Admin> findById(Long aLong);
    Optional<Admin> findByNpp(String npp);
    @Query("SELECT a FROM Admin a WHERE a.user.username = :username")
    Admin findByUsername(String username);
}
