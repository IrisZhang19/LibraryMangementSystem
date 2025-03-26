package com.librarymanagement.project.security;


import com.librarymanagement.project.models.AppRole;
import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.RoleRepository;
import com.librarymanagement.project.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                    return roleRepository.save(newAdminRole);
                });

        Set<Role> userRoles = Set.of(userRole);
        Set<Role> adminRoles = Set.of(userRole, adminRole);


        // Create users if not already present
        if (!userRepository.existsByUserName("user1")) {
            User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
            userRepository.save(user1);
            }
//
        if (!userRepository.existsByUserName("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
            userRepository.save(admin);
        }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
    }
}
