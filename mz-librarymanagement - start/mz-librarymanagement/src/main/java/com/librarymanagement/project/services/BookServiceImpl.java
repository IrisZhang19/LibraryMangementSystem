package com.librarymanagement.project.services;


import com.librarymanagement.project.exceptions.BusinessException;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.exceptions.ValidationException;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing {@link Book} entities.
 * This service provides methods for adding, updating, deleting, and searching for books.
 */
@Service
public class BookServiceImpl implements  BookService{

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Adds a new book to the system and associates it with a category.
     *
     * @param categoryId The ID of the category the book will belong to.
     * @param bookDTO The {@link BookDTO} containing the book details.
     * @return The added {@link BookDTO}.
     */
    @Transactional
    @Override
    public BookDTO addBook(Long categoryId, BookDTO bookDTO) {
        // Check if the category exists
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No categories found by category id: " + categoryId));

        // Check if the new title is valid
        if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
            throw new ValidationException("Book title must not be empty");
        }

        // Check if copies are set correctly
        if( bookDTO.getCopiesTotal() <= 0){
            throw new ValidationException("Total copies must be provided and more than 0");
        }

        bookDTO.setCategory(category);
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }


    /**
     * Retrieves a paginated list of all books in the system.
     * The results can be sorted by a specific field and order.
     *
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing the paginated book list and metadata.
     */
    @Override
    public BookResponse getAllBooks(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Set sort order
        Sort sortByAnyOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Get paginated books
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAnyOrder);
        Page<Book> pageBooks = bookRepository.findAll(pageDetails);
        List<Book> books = pageBooks.getContent();

        // Map to BookDTOs
        List<BookDTO> bookDTOS = books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());

        // Construct and return BookResponse
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(bookDTOS);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalElements(pageBooks.getTotalElements());
        bookResponse.setTotalPages(pageBooks.getTotalPages());
        bookResponse.setLastPage(pageBooks.isLast());
        return bookResponse;
    }

    /**
     * Deletes a book from the system by its ID.
     *
     * @param bookId The ID of the book to delete.
     * @return The deleted {@link BookDTO}.
     */
    @Override
    public BookDTO deleteBook(Long bookId) {
        // Fetch the book from database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No books found by book id :"  + bookId));

        // Mark the book as inactive, soft deletion
       book.setActive(false);
       Book savedBook = bookRepository.save(book);
       return modelMapper.map(savedBook, BookDTO.class);
    }

    /**
     * Updates the details of an existing book.
     *
     * @param bookId The ID of the book to update.
     * @param bookDTO The {@link BookDTO} containing the updated book details.
     * @return The updated {@link BookDTO}.
     */
    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        // Find the book
        Book bookFromDB = bookRepository.findById(bookId)
                .orElseThrow(()-> new ResourceNotFoundException("No books found"));

        // Check if it's an inactive book
        if(!bookFromDB.isActive()){
            throw new BusinessException("Cannot update inactive book");
        }

        // Check if the new name is valid
        if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
            throw new ValidationException("Book title must not be empty");
        }

        // Check if copies are set correctly
        if( bookDTO.getCopiesTotal() < bookDTO.getCopiesAvailable()){
            throw new ValidationException("Total copies cannot be less than available copies");
        }

        // Update the properties
        Book book = modelMapper.map(bookDTO, Book.class);
        bookFromDB.setTitle(book.getTitle());
        bookFromDB.setAuthor(book.getAuthor());
        bookFromDB.setDescription(book.getDescription());
        bookFromDB.setCopiesTotal(book.getCopiesTotal());
        bookFromDB.setCopiesAvailable(book.getCopiesTotal() - book.getCopiesBorrowed());
        bookFromDB.setCopiesBorrowed(book.getCopiesBorrowed());
        bookFromDB.setActive(true); // only deletion can mark a book as inactive
        if(book.getCategory() != null){
            Category category = categoryRepository.findById(book.getCategory().getCategoryId())
                    .orElseThrow( () -> new ResourceNotFoundException("No categories found by category id : "
                            + book.getCategory().getCategoryId()));
            bookFromDB.setCategory(category);
        }

        Book savedBook = bookRepository.save(bookFromDB);

        return modelMapper.map(savedBook, BookDTO.class);
    }

    /**
     * Partially updates the details of an existing book.
     * Do not need all the fields in the input BookDTO.
     *
     * @param bookId The ID of the book to update.
     * @param bookDTO The {@link BookDTO} containing the updated book details.
     * @return The updated {@link BookDTO}.
     */
    @Override
    public BookDTO partialUpdateBook(Long bookId, BookDTO bookDTO) {
        // Fetch the book
        Book bookFromDB = bookRepository.findById(bookId)
                .orElseThrow(()-> new ResourceNotFoundException("No books found by id : "  + bookId));

        // Check if it's an inactive book
        if(!bookFromDB.isActive()){
            throw new BusinessException("Cannot update inactive book");
        }

        // Update and construct the book
        if (bookDTO.getTitle() != null && !bookDTO.getTitle().trim().isEmpty()) {
            bookFromDB.setTitle(bookDTO.getTitle());
        }

        if(bookDTO.getAuthor() != null && !bookDTO.getAuthor().trim().isEmpty()){
            bookFromDB.setAuthor(bookDTO.getAuthor());
        }

        if(bookDTO.getCopiesTotal() > 0){
            bookFromDB.setCopiesTotal(bookDTO.getCopiesTotal());
            if(bookDTO.getCopiesTotal() < bookFromDB.getCopiesBorrowed()){
                throw new ValidationException("Total copies cannot be less than borrowed copies");
            }

            bookFromDB.setCopiesAvailable(bookFromDB.getCopiesTotal() - bookFromDB.getCopiesBorrowed());
        }

        if(bookDTO.getDescription() != null && !bookDTO.getDescription().trim().isEmpty()){
            bookFromDB.setDescription(bookDTO.getDescription());
        }

        if(bookDTO.getCategory() != null && categoryRepository.existsById(bookDTO.getCategory().getCategoryId())){
            Category category = categoryRepository.findById(bookDTO.getCategory().getCategoryId()).orElseThrow();
            bookFromDB.setCategory(category);
        }

        Book savedBook = bookRepository.save(bookFromDB);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    /**
     * Retrieves a paginated list of books by category.
     *
     * @param categoryId The ID of the category to filter books by.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing the filtered book list and metadata.
     */
    @Override
    public BookResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No categories found"));


        // Construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Book> pageBooks = bookRepository.findByCategory(category, pageDetails);
        List<Book> books = pageBooks.getContent();

        // Construct response DTO
        List<BookDTO> bookDTOS = books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(bookDTOS);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalElements(pageBooks.getTotalElements());
        bookResponse.setTotalPages(pageBooks.getTotalPages());
        bookResponse.setLastPage(pageBooks.isLast());
        return bookResponse;
    }

    /**
     * Retrieves a paginated list of books by author.
     *
     * @param author The author's name to filter books by.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing the filtered book list and metadata.
     */
    @Override
    public BookResponse searchByAuthor(String author, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Fetch books by title
        Page<Book> pageBooks = bookRepository.findByAuthorContainingIgnoreCase(author, pageDetails);

        // Construct Response and return
        List<BookDTO> bookDTOS = pageBooks.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(bookDTOS);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalElements(pageBooks.getTotalElements());
        bookResponse.setTotalPages(pageBooks.getTotalPages());
        bookResponse.setLastPage(pageBooks.isLast());
        return bookResponse;
    }

    /**
     * Retrieves a paginated list of books by title.
     *
     * @param title The title or partial title to filter books by.
     * @param pageNumber The page number to retrieve (starting from 0).
     * @param pageSize The number of books per page.
     * @param sortBy The field to sort by (e.g., title, author).
     * @param sortOrder The order of sorting, either ascending ("asc") or descending ("desc").
     * @return A {@link BookResponse} containing the filtered book list and metadata.
     */
    @Override
    public BookResponse searchByTitle(String title, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // get books
        Page<Book> pageBooks = bookRepository.findByTitleLikeIgnoreCase('%' + title + '%', pageDetails);

        // Construct Response and return
        List<BookDTO> bookDTOS = pageBooks.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(bookDTOS);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalElements(pageBooks.getTotalElements());
        bookResponse.setTotalPages(pageBooks.getTotalPages());
        bookResponse.setLastPage(pageBooks.isLast());
        return bookResponse;
    }

}
