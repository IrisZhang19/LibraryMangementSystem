package com.librarymanagement.project.services;


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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements  BookService{
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookDTO addBook(Long categoryId, BookDTO bookDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->  new RuntimeException("category not exist"));
        Book book = modelMapper.map(bookDTO, Book.class);
        book.setCategory(category);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public BookResponse getAllBooks(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAnyOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAnyOrder);
        Page<Book> pageBooks = bookRepository.findAll(pageDetails);

        List<Book> books = pageBooks.getContent();
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

    @Override
    public BookDTO deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("book not found"));
        bookRepository.deleteById(bookId);
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        // find the book
        Book bookFromDB = bookRepository.findById(bookId)
                .orElseThrow(()-> new RuntimeException("book not found"));

        // update the properties
        Book book = modelMapper.map(bookDTO, Book.class);
        bookFromDB.setTitle(book.getTitle());
        bookFromDB.setAuthor(book.getAuthor());
        bookFromDB.setDescription(book.getDescription());
        bookFromDB.setCopiesTotal(book.getCopiesTotal());
        bookFromDB.setCopiesAvailable(book.getCopiesAvailable());
        if(book.getCategory() != null){
            Category category = categoryRepository.findById(book.getCategory().getCategoryId())
                    .orElseThrow( () -> new RuntimeException("category not found"));
            bookFromDB.setCategory(category);
        }

        Book savedBook = bookRepository.save(bookFromDB);

        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public BookResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow( () -> new RuntimeException("category not found"));

        // construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Book> pageBooks = bookRepository.findByCategory(category, pageDetails);

        List<Book> books = pageBooks.getContent();
        // construct response DTO
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

    @Override
    public BookResponse searchByAuthor(String author, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Book> pageBooks = bookRepository.findByAuthorContainingIgnoreCase(author, pageDetails);

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

    @Override
    public BookResponse searchByTitle(String title, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // construct page
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // get books
        Page<Book> pageBooks = bookRepository.findByTitleLikeIgnoreCase('%' + title + '%', pageDetails);

        // construct response DTO
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
