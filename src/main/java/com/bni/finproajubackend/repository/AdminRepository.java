package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByNpp(String npp);
}
