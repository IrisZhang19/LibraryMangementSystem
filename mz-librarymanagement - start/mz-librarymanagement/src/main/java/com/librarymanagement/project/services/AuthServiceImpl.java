package com.librarymanagement.project.services;


import com.librarymanagement.project.models.AppRole;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public MessageResponse signupUser(SignupRequest signUpRequest) {
        // check if username and email are already exists
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())); // make sure password is encoded

//        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

//        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role User not found"));
            roles.add(userRole);
//        }

        user.setRoles(roles);
        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    @Override
    public MessageResponse signupAdmin(SignupRequest signUpRequest) {
        // Check if username and email are unique
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            new MessageResponse("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
           new MessageResponse("Error: Email is already in use!");
        }

        // Create new admin account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(adminRole));

        userRepository.save(user);

        return new MessageResponse("Admin registered successfully!");
    }
}
