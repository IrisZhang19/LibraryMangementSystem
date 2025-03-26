package com.librarymanagement.project.services;

import com.librarymanagement.project.payloads.TransactionDTO;

/**
 * Service interface for handling book borrow and return operations.
 * Defines methods for borrowing and returning books.
 */
public interface BorrowReturnService {

    /**
     * Borrows a book by its ID.
     *
     * @param bookId the ID of the book to be borrowed.
     * @return a TransactionDTO containing the transaction details of the borrow action.
     */
    public TransactionDTO borrowBook(Long bookId);

    /**
     * Returns a book by its ID.
     *
     * @param bookId the ID of the book to be returned.
     * @return a TransactionDTO containing the transaction details of the borrow action.
     */
    public TransactionDTO returnBook(Long bookId);
}
