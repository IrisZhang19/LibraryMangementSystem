package com.librarymanagement.project.services;

import com.librarymanagement.project.security.jwt.LoginRequest;
import com.librarymanagement.project.security.jwt.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> signin(LoginRequest loginRequest);

    ResponseEntity<?> signupUser(@Valid SignupRequest signUpRequest);

    ResponseEntity<?> signupAdmin(@Valid SignupRequest signUpRequest);
}
