package com.librarymanagement.project.services;

import com.librarymanagement.project.security.jwt.MessageResponse;
import com.librarymanagement.project.security.jwt.SigninRequest;
import com.librarymanagement.project.security.jwt.SignupRequest;
import com.librarymanagement.project.security.jwt.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;



/**
 * Service interface for handling authentication and user signup.
 */
public interface AuthService {

    /**
     * Authenticates a user based on their credentials (username and password).
     *
     * @param signinRequest contains the username and password of the user trying to sign in.
     * @return a UserInfoResponse containing the authenticated user's details, including a JWT token.
     */
    UserInfoResponse signin(SigninRequest signinRequest);

    /**
     * Signs up a new regular user.
     *
     * @param signUpRequest contains the user details for the signup action.
     * @return a MessageResponse containing a success or error message.
     */
    MessageResponse signupUser(@Valid SignupRequest signUpRequest);

    /**
     * Signs up a new admin.
     * This method can only be accessed by users with the 'ADMIN' role.
     *
     * @param signUpRequest contains the admin details for the signup action.
     * @return a MessageResponse containing a success or error message.
     */
    MessageResponse signupAdmin(@Valid SignupRequest signUpRequest);
}
