package com.librarymanagement.project.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


/**
 * Represents a User entity in the library management system.
 * This entity stores information about users.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_name"),
                @UniqueConstraint(columnNames = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * The unique identifier for each user.
     * This ID is automatically generated and serves as the primary key for the User entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * The username of the user.
     * This field is unique, not null, and has a maximum length of 20 characters.
     */
    @NotBlank
    @Size(max = 20)
    @Column(name = "user_name")
    private String userName;

    /**
     * The password of the user.
     * This field is not blank.
     */
    @NotBlank
    @Column(name = "password")
    private String password;

    /**
     * The email of the user.
     * This field is unique, must be a valid email format, and has a maximum length of 50 characters.
     */
    @Email
    @NotBlank
    @Size(max = 50)
    @Column(name = "email")
    private String email;

    /**
     * Constructor to create a new User with a specified username, email, and password.
     * This constructor is used when a new user is registered.
     *
     * @param userName The username of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    /**
     * The roles assigned to the user.
     * Each user can have multiple roles, and roles are managed using a many-to-many relationship.
     */
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}


