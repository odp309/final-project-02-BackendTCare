package com.bni.finproajubackend.service;

import com.bni.finproajubackend.controller.middleware.exceptions.InvalidUserException;
import com.bni.finproajubackend.dto.user.UserRequestDTO;
import com.bni.finproajubackend.interfaces.UserInterface;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserRequestDTO request, Authentication authentication) {
        User loginUser = userRepository.findByUsername(authentication.getName());

        if (!loginUser.getAdmin().getRole().getRoleName().equals("admin"))
            throw new InvalidUserException("Not Permitted to add User");

        User user = userRepository.findByUsername(request.getUsername());

        if (user != null)
            throw new InvalidUserException("Username already exist!");

        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setAge(request.getAge());
        admin.setNoHP(request.getNoHP());
        admin.setAddress(request.getAddress());
        admin.setGender(request.getGender());

        user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAdmin(admin);

        userRepository.save(user);

        return user;
    }

    @Override
    public User editUser(String username, UserRequestDTO request) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;
        user.setUsername(request.getUsername());
        if (user.getAdmin() != null) {
            user.getAdmin().setFirstName(request.getFirstName());
            user.getAdmin().setLastName(request.getLastName());
        }
        if (user.getNasabah() != null) {
            user.getNasabah().setFirstName(request.getFirstName());
            user.getNasabah().setLastName(request.getLastName());
        }
        userRepository.save(user);
        return user;
    }

    @Override
    public boolean deleteUser(String username) {
        if (!userRepository.existsByUsername(username)) return false;
        int res = userRepository.deleteByUsername(username);
        return res == 1;
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
