package com.librarymanagement.project.services;

import com.librarymanagement.project.payloads.TransactionDTO;

public interface BorrowReturnService {
    public TransactionDTO borrowBook(Long bookId);

    public TransactionDTO returnBook(Long bookId);
}
