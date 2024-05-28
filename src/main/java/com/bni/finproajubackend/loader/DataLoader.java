package com.bni.finproajubackend.loader;
import com.bni.finproajubackend.model.enumobject.Gender;
import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.admin.Permission;
import com.bni.finproajubackend.model.user.admin.Role;
import com.bni.finproajubackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class DataLoader {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PersonRepository personRepository, AdminRepository adminRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            loadAdmin();
        };
    }

    private void loadAdmin(){
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername("admin"));
        if (existingUser.isPresent()) {
            return;
        }

        // Menambahkan permission
        Permission permission = new Permission();
        permission.setPermissionName("all");
        permission.setPermissionDescription("Bisa melakukan semuanya");
        permissionRepository.save(permission);

        // Menambahkan role
        Role role = new Role();
        role.setRoleName("admin");
        role.setRoleDescription("Role untuk admin");
        role.setPermissions(Collections.singletonList(permission));
        roleRepository.save(role);

        // Menambahkan user
        User user = new User("admin", passwordEncoder.encode("123456"));

        // Menambahkan person
        Person person = new Person();
        person.setFirstName("Jakarta");
        person.setAge(25);
        person.setEmail("ex@gmail.com");
        person.setLastName("zigax");
        person.setGender(Gender.Male);
        person.setAddress("arcadia");
        person.setNoHP("081249149989");
        person.setUser(user);
        user.setPerson(person);
        userRepository.save(user);
        personRepository.save(person);

        // Menambahkan admin
        Admin admin = new Admin();
        admin.setNpp("T094459");
        admin.setPerson(person);
        admin.setRole(role);
        adminRepository.save(admin);
    }
}
