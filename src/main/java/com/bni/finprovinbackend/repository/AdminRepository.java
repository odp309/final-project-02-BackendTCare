package com.bni.finprovinbackend.repository;

import com.bni.finprovinbackend.model.user.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByNpp(String npp);
}
