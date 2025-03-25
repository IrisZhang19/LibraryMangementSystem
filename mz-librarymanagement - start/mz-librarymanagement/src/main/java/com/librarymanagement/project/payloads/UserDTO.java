package com.librarymanagement.project.payloads;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String email;

    // Dummy setter to avoid ModelMapper errors
    public void setPassword(String password) {}
}
