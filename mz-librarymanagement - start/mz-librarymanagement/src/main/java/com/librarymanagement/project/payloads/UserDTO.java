package com.librarymanagement.project.payloads;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for User entity.
 * Used to transfer category data between different layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * The unique identifier for the user.
     */
    private Long userId;

    /**
     * The username of the user.
     * This field should be unique.
     */
    private String userName;

    /**
     * The email address of the user.
     * This field contains the user's email used for login and should be unique.
     */
    private String email;

    /**
     * Dummy setter to avoid errors when using ModelMapper.
     * @param password The password to set (not used in this DTO).
     */
    public void setPassword(String password) {}
}
