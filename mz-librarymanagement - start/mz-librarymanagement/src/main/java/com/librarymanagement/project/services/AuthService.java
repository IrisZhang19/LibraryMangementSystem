package com.librarymanagement.project.services;

import com.librarymanagement.project.security.jwt.MessageResponse;
import com.librarymanagement.project.security.jwt.SigninRequest;
import com.librarymanagement.project.security.jwt.SignupRequest;
import com.librarymanagement.project.security.jwt.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    UserInfoResponse signin(SigninRequest signinRequest);

    MessageResponse signupUser(@Valid SignupRequest signUpRequest);

    MessageResponse signupAdmin(@Valid SignupRequest signUpRequest);
}
