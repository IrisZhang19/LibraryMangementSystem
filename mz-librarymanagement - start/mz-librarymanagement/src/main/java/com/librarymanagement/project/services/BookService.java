package com.librarymanagement.project.services;

import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;

/**
 * Service interface for managing Book related operations.
 * Defines methods for creating, updating, deleting, and retrieving books.
 */
public interface BookService {

    /**
     * Adds a new book to the system.
     *
     * @param categoryId The ID of the category to which the book will belong.
     * @param bookDTO The data transfer object containing the book's details.
     * @return The {@link BookDTO} of the added book.
     */
    BookDTO addBook(Long categoryId, BookDTO bookDTO);

    /**
     * Retrieves a paginated list of all books in the system.
     *
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort the results by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing a list of books and pagination details.
     */
    BookResponse getAllBooks(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Deletes a book from the system based on the book's ID.
     *
     * @param bookId The ID of the book to delete.
     * @return The {@link BookDTO} of the deleted book.
     */
    BookDTO deleteBook(Long bookId);

    /**
     * Updates the details of an existing book in the system.
     *
     * @param bookId The ID of the book to update.
     * @param bookDTO The data transfer object containing the updated book details.
     * @return The {@link BookDTO} of the updated book.
     */
    BookDTO updateBook(Long bookId, BookDTO bookDTO);

    /**
     * Partially updates the details of an existing book.
     * Do not need all the fields in the input BookDTO.
     *
     * @param bookId The ID of the book to update.
     * @param bookDTO The {@link BookDTO} containing the updated book details.
     * @return The updated {@link BookDTO}.
     */
    BookDTO partialUpdateBook(Long bookId, BookDTO bookDTO);

    /**
     * Retrieves a paginated list of books that belong to a specific category.
     *
     * @param categoryId The ID of the category to filter books by.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort the results by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing a list of books in the specified category and pagination details.
     */
    BookResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Retrieves a paginated list of books by a specific author.
     *
     * @param author The author's name to search for.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort the results by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing a list of books by the specified author and pagination details.
     */
    BookResponse searchByAuthor(String author, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Retrieves a paginated list of books by their title.
     *
     * @param title The title or partial title to search for.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort the results by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing a list of books with titles matching the search term and pagination details.
     */
    BookResponse searchByTitle(String title, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);


}
