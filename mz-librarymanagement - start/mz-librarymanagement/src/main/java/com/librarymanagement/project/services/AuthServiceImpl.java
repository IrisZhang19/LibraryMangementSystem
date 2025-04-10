package com.librarymanagement.project.services;


import com.librarymanagement.project.exceptions.BusinessException;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.models.AppRole;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.RoleRepository;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.jwt.*;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for handling authentication and user signup.
 */
@Service
public class AuthServiceImpl implements  AuthService{

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login request containing the username and password of the user.
     * @return UserInfoResponse containing user details and JWT token.
     * @throws BadCredentialsException if authentication fails due to incorrect username or password.
     */
    @Override
    public UserInfoResponse signin(SigninRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Bad credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles, jwtToken);

        return response;
    }

    /**
     * Signs up a new regular user.
     *
     * @param signUpRequest contains the details of the user (username, email, password) for signup.
     * @return MessageResponse containing a success or error message.
     */
//    @Transactional
    @Override
    public MessageResponse signupUser(SignupRequest signUpRequest) {
        // check if username and email are already exists
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new BusinessException("Error: Username " +  signUpRequest.getUsername() + " is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException("Error: This Email " + signUpRequest.getEmail() +  " is already registered!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())); // make sure password is encoded

        Set<Role> roles = new HashSet<>();

            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role User not found"));
            roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    /**
     * Signs up a new admin user.
     *
     * @param signUpRequest contains the details of the admin user (username, email, password) for signup.
     * @return MessageResponse containing a success or error message.
     */
    @Transactional
    @Override
    public MessageResponse signupAdmin(SignupRequest signUpRequest) {
        // check if username and email are already exists
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new BusinessException("Error: Username " +  signUpRequest.getUsername() + " is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException("Error: This Email " + signUpRequest.getEmail() +  " is already registered!");
        }

        // Create new admin account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role Admin not found"));
        user.setRoles(Collections.singleton(adminRole));

        userRepository.save(user);

        return new MessageResponse("Admin registered successfully!");
    }
}
