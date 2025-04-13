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


/**
 * DataInitializer is responsible for initializing default roles and users in the database
 * during the application startup. It ensures that the default roles (USER and ADMIN) exist
 * and assigns them to the respective users (user1 and admin) if they are not already present.
 */
@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method is called after the bean is created to initialize the default roles and users.
     * It checks whether the roles (USER and ADMIN) exist and creates them if necessary.
     * It also creates the default users (user1 and admin) and assigns the appropriate roles.
     */
    @PostConstruct
    public void init(){
        // Create user and admin roles
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role();
                        newUserRole.setRoleName(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newAdminRole = new Role();
                    newAdminRole.setRoleName(AppRole.ROLE_ADMIN);
                    return roleRepository.save(newAdminRole);
                });

        Set<Role> userRoles = Set.of(userRole);
        Set<Role> adminRoles = Set.of(adminRole, userRole);


        // Create users if not already present
        if (!userRepository.existsByUserName("user1")) {
            User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
            userRepository.save(user1);
            }

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
