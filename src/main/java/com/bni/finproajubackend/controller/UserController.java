package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.annotation.RequirePermission;
import com.bni.finproajubackend.dto.user.UserRequestDTO;
import com.bni.finproajubackend.dto.user.UserResponseDTO;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import com.bni.finproajubackend.interfaces.UserInterface;
import com.bni.finproajubackend.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/private/user")
public class UserController {
    @Autowired
    private UserInterface userService;

    @Autowired
    private TemplateResInterface responseService;

    @RequirePermission("getAllUser")
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity getUser() {
        List<User> listUser = userService.getUsers();
        return ResponseEntity.ok(responseService.apiSuccess(listUser));
    }

    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity getDetailUser(Authentication authentication) {
        User userData = userService.getUser(authentication.getName());
        if (userData == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest("User Is Not Available"));
        UserResponseDTO userDetails = new UserResponseDTO(userData.getUsername(), userData.getPerson().getFirstName(), userData.getPerson().getLastName());
        return ResponseEntity.ok(responseService.apiSuccess(userDetails));
    }

    @RequirePermission("addUser")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity addUser(@RequestBody UserRequestDTO req, Authentication authentication) {
        try {
            User result = userService.createUser(req, authentication);
            return ResponseEntity.ok(responseService.apiSuccess(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.apiBadRequest(e));
        }
    }

    @RequirePermission("updateUser")
    @PutMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<User> editUser(@PathVariable String username, @RequestBody UserRequestDTO req) {
        User result = userService.editUser(username, req);
//        if(result == null) return ResponseEntity.ok("User Not Found");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        boolean result = userService.deleteUser(username);
        if (!result) return ResponseEntity.ok("Failed or User does not exist");
        return ResponseEntity.ok("User Deleted");
    }
}
