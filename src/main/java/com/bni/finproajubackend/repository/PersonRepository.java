package com.bni.finproajubackend.repository;

import com.bni.finproajubackend.model.user.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByUser_Id(Long userID);
}
