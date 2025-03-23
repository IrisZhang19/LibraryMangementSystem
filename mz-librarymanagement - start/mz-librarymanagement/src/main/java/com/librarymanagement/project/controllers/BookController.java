package com.librarymanagement.project.controllers;


import com.librarymanagement.project.configs.AppConstants;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle book-related operations in the library management system.
 * Provides API endpoints for adding, retrieving, updating, and deleting books.
 */
@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    BookService bookService;

    /**
     * Adds a new book to a specific category.
     *
     * @param bookDTO the book data transfer object containing the book details.
     * @param categoryId the ID of the category to which the book will be added.
     * @return the added BookDTO.
     */
    @PostMapping("/admin/categories/{categoryId}/book")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO, @PathVariable Long categoryId){
        BookDTO addedBook = bookService.addBook(categoryId, bookDTO);
        return new ResponseEntity<>(addedBook, HttpStatus.OK);
    }

    /**
     * Retrieves all books with pagination and sorting options.
     *
     * @param pageNumber the page number to retrieve (default: 0).
     * @param pageSize the number of books per page (default: 10).
     * @param sortBy the field by which the books should be sorted (default: "title").
     * @param sortOrder the sorting order, either ascending ("asc") or descending ("desc") (default: "asc").
     * @return a response containing a list of books and pagination metadata.
     */
    @GetMapping("/public/books")
    public ResponseEntity<BookResponse> getAllBooks(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_BOOKS_BY) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        BookResponse bookResponse = bookService.getAllBooks(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    /**
     * Retrieves books by a specific category with pagination and sorting options.
     *
     * @param categoryId the ID of the category to filter books by.
     * @param pageNumber the page number to retrieve (default: 0).
     * @param pageSize the number of books per page (default: 10).
     * @param sortBy the field by which the books should be sorted (default: "title").
     * @param sortOrder the sorting order, either ascending ("asc") or descending ("desc") (default: "asc").
     * @return a response containing a list of books filtered by category and pagination metadata.
     */
    @GetMapping("/public/categories/{categoryId}/books")
    public ResponseEntity<BookResponse> getBooksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_BOOKS_BY) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        BookResponse bookResponse = bookService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    /**
     * Retrieves books by author with pagination and sorting options.
     *
     * @param author the name of the author to filter books by.
     * @param pageNumber the page number to retrieve (default: 0).
     * @param pageSize the number of books per page (default: 10).
     * @param sortBy the field by which the books should be sorted (default: "title").
     * @param sortOrder the sorting order, either ascending ("asc") or descending ("desc") (default: "asc").
     * @return a response containing a list of books filtered by author and pagination metadata.
     */
    @GetMapping("/public/books/author")
    public ResponseEntity<BookResponse> getBooksByAuthor(
        @RequestParam(name = "author") String author,
        @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
        @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
        @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_BOOKS_BY) String sortBy,
        @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR) String sortOrder
    ) {
        BookResponse bookResponse = bookService.searchByAuthor(author, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    /**
     * Retrieves books by title with pagination and sorting options.
     *
     * @param title the title or part of the title to filter books by.
     * @param pageNumber the page number to retrieve (default: 0).
     * @param pageSize the number of books per page (default: 10).
     * @param sortBy the field by which the books should be sorted (default: "title").
     * @param sortOrder the sorting order, either ascending ("asc") or descending ("desc") (default: "asc").
     * @return a response containing a list of books filtered by title and pagination metadata.
     */
    @GetMapping("/public/books/title")
    public ResponseEntity<BookResponse> getBooksByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_BOOKS_BY) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR) String sortOrder
    ) {
        BookResponse bookResponse = bookService.searchByTitle(title, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId the ID of the book to delete.
     * @return the BookDTO of the deleted book.
     */
    @DeleteMapping("/admin/books/{bookId}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable Long bookId){
        BookDTO bookDTO = bookService.deleteBook(bookId);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    /**
     * Updates a book's information by its ID.
     *
     * @param bookId the ID of the book to update.
     * @param bookDTO the new book data transfer object with updated details.
     * @return the updated BookDTO.
     */
    @PutMapping("/admin/books/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId,
                                              @RequestBody BookDTO bookDTO){
        BookDTO updatedBookDTO = bookService.updateBook(bookId, bookDTO);
        return new ResponseEntity<>(updatedBookDTO, HttpStatus.OK);
    }
}
