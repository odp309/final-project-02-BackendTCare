package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.admin.AdminResponseDTO;
import com.bni.finproajubackend.interfaces.AdminInterface;
import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.repository.PersonRepository;
import com.bni.finproajubackend.repository.UserRepository;
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
    public AdminResponseDTO getAdminProfile(Authentication  authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Person person = personRepository.findByUser(user);

        if (person == null || person.getAdmin() == null) {
            throw new RuntimeException("Person or Admin data not found for user: " + username);
        }

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
