package com.librarymanagement.project.services;

import com.librarymanagement.project.payloads.TranscationDTO;

public interface BorrowReturnService {
    public TranscationDTO borrowBook(Long bookId);
}
