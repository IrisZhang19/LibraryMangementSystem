package com.librarymanagement.project.security.jwt;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object (DTO) to representing sign up request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    /**
     * The name of the signup request.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /**
     * The email of the user trying to signup.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * The password user intended to set up
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}