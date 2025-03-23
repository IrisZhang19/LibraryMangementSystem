package com.librarymanagement.project.controllers;


import com.librarymanagement.project.configs.AppConstants;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    BookService bookService;


    @PostMapping("/admin/categories/{categoryId}/book")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO, @PathVariable Long categoryId){
        BookDTO addedBook = bookService.addBook(categoryId, bookDTO);
        return new ResponseEntity<>(addedBook, HttpStatus.OK);
    }

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

//    @PutMapping


    @DeleteMapping("/admin/books/{bookId}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable Long bookId){
        BookDTO bookDTO = bookService.deleteBook(bookId);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/books/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId,
                                              @RequestBody BookDTO bookDTO){
        BookDTO updatedBookDTO = bookService.updateBook(bookId, bookDTO);
        return new ResponseEntity<>(updatedBookDTO, HttpStatus.OK);
    }
}
