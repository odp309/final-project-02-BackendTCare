package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.admin.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findById(long id);
}