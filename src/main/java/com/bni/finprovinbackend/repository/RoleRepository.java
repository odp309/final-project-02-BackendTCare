package com.bni.finprovinbackend.repository;

import com.bni.finprovinbackend.model.user.admin.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findById(long id);
}
