package com.bni.finproajubackend.repository;

//import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NasabahRepository extends JpaRepository<Nasabah, Long> {
    Nasabah findByUser(User user);
    Optional<Nasabah> findByEmail(String email);

    @Query("SELECT n FROM Nasabah n WHERE n.user.username = :username")
    Nasabah findByUsername(@Param("username") String username);
}
