package com.librarymanagement.project.controllers;

import com.librarymanagement.project.security.jwt.*;
import com.librarymanagement.project.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication-related actions.
 * Provides endpoints including user siging in, regular user signing up and admin signing up.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    /**
     * Endpoint for authenticating a user (signing in).
     *
     * @param loginRequest contains the credentials (username and password) of the user.
     * @return a UserInfoResponse containing user details if authentication is successful.
     */
    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> authenticateUser(@RequestBody SigninRequest loginRequest) {
        UserInfoResponse userInfoResponse =  authService.signin(loginRequest);
        return new ResponseEntity<>( userInfoResponse, HttpStatus.OK );
    }

    /**
     * Endpoint for signing up a new user.
     *
     * @param signUpRequest contains the details of the user to be registered.
     * @return a MessageResponse containing a success message.
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest  signUpRequest) {
        MessageResponse userInfoResponse = authService.signupUser(signUpRequest);
        return new ResponseEntity<>(userInfoResponse, HttpStatus.CREATED);

    }

    /**
     * Endpoint for registering a new admin.
     * Only accessible by users with the 'ADMIN' role.
     *
     * @param signUpRequest contains the details of the admin to be registered.
     * @return a MessageResponse containing a success message.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/signup")
    public ResponseEntity<MessageResponse> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse messageResponse = authService.signupAdmin(signUpRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

}
