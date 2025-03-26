package com.librarymanagement.project.repositories;


import com.librarymanagement.project.models.AppRole;
import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a user by their username.
     *
     * @param appRole The name of the role to search for.
     * @return An Optional containing the Role if found, or an empty Optional if no role is found.
     */
    Optional<Role> findByRoleName(AppRole appRole);

}