package com.librarymanagement.project.models;


import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


/**
 * Represents a Transaction entity in the library management system.
 * This entity stores information about borrowing and returning a book.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * The unique identifier for the transaction.
     */
    @Id
    @GeneratedValue
    @Column(name = "transaction_id")
    private Long transactionId;

    /**
     * The date when the book was borrowed.
     */
    @Column(name = "borrowed_date")
    private LocalDate borrowedDate;

    /**
     * The date when the book was returned.
     * This can be null initially until the book is returned.
     */
    @Nullable  // The return date can be null initially
    @Column(name = "returned_date")
    private LocalDate returnedDate;

    /**
     * Shows whether the book has been returned.
     * Defaults to false (not returned).
     */
    @Column(name = "is_returned")
    private boolean isReturned = false;

    /**
     * The book that corresponds to this transaction.
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * The user that corresponds to this transaction
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
