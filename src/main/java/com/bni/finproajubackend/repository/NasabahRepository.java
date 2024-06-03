package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NasabahRepository extends JpaRepository<Nasabah, Long> {
    Nasabah findByPerson(Person person);
}
