package com.librarymanagement.project.security.jwt;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) to representing sign in request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequest {

    /**
     * The name of the user to sign in.
     */
    private String username;

    /**
     * The password of sign in request.
     */
    private String password;

}
