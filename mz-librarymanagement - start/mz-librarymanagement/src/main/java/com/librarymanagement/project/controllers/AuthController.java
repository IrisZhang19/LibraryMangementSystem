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


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> authenticateUser(@RequestBody SigninRequest loginRequest) {
        UserInfoResponse userInfoResponse =  authService.signin(loginRequest);
        return new ResponseEntity<>( userInfoResponse, HttpStatus.OK );
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest  signUpRequest) {
        MessageResponse userInfoResponse = authService.signupUser(signUpRequest);
        return new ResponseEntity<>(userInfoResponse, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/signup")
    public ResponseEntity<MessageResponse> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse messageResponse = authService.signupAdmin(signUpRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.CREATED);
    }

}
