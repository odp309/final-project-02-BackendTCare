package com.bni.finprovinbackend.service;

import com.bni.finprovinbackend.dto.admin.AdminResponseDTO;
import com.bni.finprovinbackend.interfaces.AdminInterface;
import com.bni.finprovinbackend.model.user.Person;
import com.bni.finprovinbackend.model.user.User;
import com.bni.finprovinbackend.repository.PersonRepository;
import com.bni.finprovinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements AdminInterface {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AdminResponseDTO getAdminProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new RuntimeException("User not found");


        Person person = personRepository.findByUser(user);

        if (person == null || person.getAdmin() == null)
            throw new RuntimeException("Person or Admin data not found for user: " + username);


        return AdminResponseDTO.builder()
                .id(person.getId())
                .email(person.getEmail())
                .noHP(person.getNoHP())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .gender(person.getGender())
                .age(person.getAge())
                .address(person.getAddress())
                .npp(person.getAdmin().getNpp())  // Include npp here
                .role(person.getAdmin().getRole())  // Include RoleDTO here
                .build();
    }
}
