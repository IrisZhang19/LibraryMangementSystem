package com.librarymanagement.project.services;

import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;

public interface BookService {
    BookDTO addBook(Long categoryId, BookDTO bookDTO);

    BookResponse getAllBooks(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BookDTO deleteBook(Long bookId);

    BookDTO updateBook(Long bookId, BookDTO bookDTO);

    BookResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BookResponse searchByAuthor(String author, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BookResponse searchByTitle(String title, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
