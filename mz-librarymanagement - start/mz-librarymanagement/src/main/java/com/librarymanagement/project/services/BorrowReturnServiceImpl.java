package com.librarymanagement.project.services;

import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Transaction;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.payloads.TransactionDTO;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.TransactionRepository;
import com.librarymanagement.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;


/**
 * Implementation of {@link BorrowReturnService} for managing category-related operations.
 * Provides methods for borrow and return books.
 */
@Service
public class BorrowReturnServiceImpl implements BorrowReturnService{

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;


    /**
     * Borrows a book for the current user.
     *
     * @param bookId the ID of the book to be borrowed.
     * @return a TransactionDTO containing the transaction details for the borrowing.
     * @throws ResponseStatusException if the book is unavailable or if the user already has the book borrowed.
     */
    @Transactional
    @Override
    public TransactionDTO borrowBook(Long bookId) {
        // Fetch the user
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found"));
        Long userId = user.getUserId();

        // Find the book and check availability
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found"));
        if(!book.isAvailable()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No copies available");
        }

        // Check if user has unreturned borrow of the same book
        Optional<Transaction> existingTranscation = transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(userId, bookId);
        if(existingTranscation.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book already borrowed by you");
        }

        // Create transaction entity and save
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setUser(user);
        transaction.setBorrowedDate(LocalDate.now());
        transaction.setReturned(false);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update book copies available
        book.borrowOneCopy();
        bookRepository.save(book);

        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }


    /**
     * Returns a book for the current user.
     *
     * @param bookId the ID of the book to be returned.
     * @return a TransactionDTO containing the transaction details for the borrowing.
     * @throws ResponseStatusException if the book is not borrowed by the current user.
     */
    @Transactional
    @Override
    public TransactionDTO returnBook(Long bookId) {
        // Fetch the user
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found"));
        Long userId = user.getUserId();

        // Check the book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found"));

        // Check if the user has a borrow record with this book
        Optional<Transaction> existingTransaction = transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(userId, bookId);
        if(!existingTransaction.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is not in borrow with the user");
        }

        // Update the transaction by returning the book
        Transaction transaction = existingTransaction.get();
        transaction.setReturned(true);
        transaction.setReturnedDate(LocalDate.now());
        transactionRepository.save(transaction);
        book.returnOneCopy();
        bookRepository.save(book);

        return modelMapper.map(transaction, TransactionDTO.class);
    }
}
