package com.bni.finprovinbackend.repository;

import com.bni.finprovinbackend.model.user.Person;
import com.bni.finprovinbackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByUser_Id(Long userID);
    Person findByUser(User user);
}
