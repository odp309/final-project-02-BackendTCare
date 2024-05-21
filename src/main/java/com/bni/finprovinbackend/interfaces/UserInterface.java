package com.bni.finprovinbackend.interfaces;

import com.bni.finprovinbackend.dto.user.UserRequestDTO;
import com.bni.finprovinbackend.model.user.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserInterface {
//    InitResponseDTO initRoleAndUser();
    User createUser(UserRequestDTO request, Authentication authentication);
    User editUser(String username, UserRequestDTO request);
    boolean deleteUser(String username);
    List<User> getUsers();
    User getUser(String username);
}
