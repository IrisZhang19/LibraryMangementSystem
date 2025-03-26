package com.librarymanagement.project.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


/**
 * Data Transfer Object (DTO) for Transaction entity.
 * Used to transfer category data between different layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    /**
     * The unique identifier for the transaction.
     */
    private Long transactionId;

    /**
     * The date when the book was borrowed.
     */
    private LocalDate borrowedDate;

    /**
     * The date when the book was returned.
     * This can be null initially until the book is returned.
     */
    private LocalDate returnedDate;

    /**
     * Shows whether the book has been returned.
     * Defaults to false (not returned).
     */
    private boolean isReturned;

    /**
     * The book that corresponds to this transaction.
     */
    private BookDTO book;

    /**
     * The user that corresponds to this transaction
     */
    private UserDTO user;
}
