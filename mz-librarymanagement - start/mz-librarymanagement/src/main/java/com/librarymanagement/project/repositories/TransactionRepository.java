package com.librarymanagement.project.repositories;

import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository interface for managing {@link Transaction} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds an active (not returned) transaction by user ID and book ID.
     *
     * @param userId the ID of the user who borrowed the book.
     * @param bookId the ID of the book that was borrowed.
     * @return an Optional containing the transaction if found, or empty if not found.
     */
    Optional<Transaction> findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(Long userId, Long bookId);

}
