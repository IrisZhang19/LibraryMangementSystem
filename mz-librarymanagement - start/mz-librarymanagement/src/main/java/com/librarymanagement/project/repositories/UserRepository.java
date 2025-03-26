package com.librarymanagement.project.repositories;


import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations for the Role entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username The username of the user to search for.
     * @return An Optional containing the User if found, or an empty Optional if no user is found.
     */
    Optional<User> findByUserName(String username);

    /**
     * Checks if a user with the given username already exists in the database.
     *
     * @param username The username to check for existence.
     * @return True if a user with the specified username exists, otherwise false.
     */
    Boolean existsByUserName(String username);

    /**
     * Checks if a user with the given email already exists in the database.
     *
     * @param email The email to check for existence.
     * @return True if a user with the specified email exists, otherwise false.
     */
    Boolean existsByEmail(String email);
}
