package com.librarymanagement.project.models;


/**
 * Enum representing the different roles that a user can have in the application.
 * The roles are used for role-based access control (RBAC) to determine what actions
 * a user is authorized to perform.
 *
 * <p>There are two primary roles in the system:</p>
 * <ul>
 *     <li><strong>ROLE_ADMIN</strong>: Grants administrative privileges to manage the system.</li>
 *     <li><strong>ROLE_USER</strong>: Grants standard user privileges to interact with the application.</li>
 * </ul>
 */
public enum AppRole {

    /**
     * Admin role with the full access to the application, except borrowing and returning books.
     */
    ROLE_ADMIN,

    /**
     * Regular user role with limited access to the application,
     * Allows retrieve, borrow and return their owen borrowed books.
     */
    ROLE_USER
}
