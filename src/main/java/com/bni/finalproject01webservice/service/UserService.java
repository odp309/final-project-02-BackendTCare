package com.bni.finalproject01webservice.service;

import com.bni.finalproject01webservice.configuration.exceptions.InvalidUserException;
import com.bni.finalproject01webservice.dto.LoginRequestDTO;
import com.bni.finalproject01webservice.dto.LoginResponseDTO;
import com.bni.finalproject01webservice.dto.RegisterRequestDTO;
import com.bni.finalproject01webservice.dto.RegisterResponseDTO;
import com.bni.finalproject01webservice.interfaces.JWTInterface;
import com.bni.finalproject01webservice.interfaces.UserInterface;
import com.bni.finalproject01webservice.model.Role;
import com.bni.finalproject01webservice.model.User;
import com.bni.finalproject01webservice.repository.RoleRepository;
import com.bni.finalproject01webservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JWTInterface jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void initRoleAndUser() {
        User currUser = userRepository.findByEmail("admin@bni.co.id");
        Role currAdminRole = roleRepository.findByRoleName("Admin");
        Role currUserRole = roleRepository.findByRoleName("User");

        Role adminRole = new Role();
        Role userRole = new Role();

        if (currAdminRole == null) {
            adminRole.setRoleName("Admin");
            adminRole.setRoleDescription("Admin role");
            roleRepository.save(adminRole);
        }

        if (currUserRole == null) {
            userRole.setRoleName("User");
            userRole.setRoleDescription("Default role for newly created record");
            roleRepository.save(userRole);
        }

        if (currUser == null) {
            Set<Role> role = new HashSet<>();
            role.add(Objects.requireNonNullElse(currAdminRole, adminRole));
            User user = new User();
            user.setEmail("admin@bni.co.id");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setFirstName("admin");
            user.setLastName("admin");
            user.setRole(role);

            userRepository.save(user);
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User data = userRepository.findByEmail(request.getEmail());

        if (data == null || !passwordEncoder.matches(request.getPassword(), data.getPassword())) {
            throw new InvalidUserException("Invalid user!");
        }

        String token = jwtService.generateToken(data);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);

        return response;
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        User currData = userRepository.findByEmail(request.getEmail());

        if (currData != null) {
            throw new InvalidUserException("Email already exist!");
        }

        Role role = roleRepository.findByRoleName("User");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setRole(userRoles);

        userRepository.save(newUser);

        String newToken = jwtService.generateToken(newUser);

        RegisterResponseDTO response = new RegisterResponseDTO();

        response.setToken(newToken);

        return response;
    }
}
