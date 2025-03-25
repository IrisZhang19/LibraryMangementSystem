package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.*;
import com.librarymanagement.project.payloads.TransactionDTO;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.TransactionRepository;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import com.librarymanagement.project.services.BorrowReturnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class BorrowReturnServiceTest {
    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private BorrowReturnService borrowReturnService;

    private Book book;
    private User user;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(AppRole.ROLE_USER));  // Your Role entity

        user = new User();
        user.setUserId(1L);
        user.setUserName("Test User");
        user.setRoles(roles);
        user.setPassword("password1");
        user.setEmail("user@test.com");
        // Convert Set<Role> to Set<GrantedAuthority> for security context
        authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toSet());

        book = new Book();
        book.setBookId(10L);
        book.setTitle("Test Book");
        book.setCopiesTotal(5);
        book.setCopiesAvailable(3);

        LocalDate borrowTime = LocalDate.now();
        transaction = new Transaction();
        transaction.setBorrowedDate(borrowTime);
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setTransactionId(1L);
        transaction.setReturned(false);
    }

    @Test
    public void TestBorrowBookSuccess(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), 10L))
                .thenReturn(Optional.empty());  // No existing borrow history
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Execute
        TransactionDTO result = borrowReturnService.borrowBook(10L);

        // Assert to check the fields with desired values
        assertNotNull(result);
        assertEquals(result.getUser().getUserId(), user.getUserId());
        assertEquals(result.getBorrowedDate(), transaction.getBorrowedDate());
        assertNull(result.getReturnedDate());
        assertFalse(result.isReturned());
        assertEquals(result.getBook().getBookId(), book.getBookId());
        assertEquals(2, book.getCopiesAvailable());  // Available copies should decrease by 1

        // Verify
        verify(bookRepository, times(1)).save(book);  // Ensure save is called on bookRepository
        verify(transactionRepository, times(1)).save(any(Transaction.class)); // Ensure save is called on transactionRepository

    }

    @Test
    public void TestReturnBookSuccess(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), 10L))
                .thenReturn(Optional.ofNullable(transaction));
        LocalDate returnDate = LocalDate.now();
        transaction.setReturnedDate(returnDate);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(bookRepository.save(book)).thenReturn(book);

        // Execute
        TransactionDTO result = borrowReturnService.returnBook(10L);

        // Assertions
        assertNotNull(result);
        assertEquals(result.getUser().getUserId(), user.getUserId());
        assertEquals(result.getBorrowedDate(), transaction.getBorrowedDate());
        assertEquals(result.getReturnedDate(), returnDate);
        assertTrue(result.isReturned());
        assertEquals(result.getBook().getBookId(), book.getBookId());
        assertEquals(4, book.getCopiesAvailable()); // Available copies should increase by 1

        // Verify book entity and transaction entity is updated once
        verify(bookRepository, times(1)).save(book); // Ensure save is called on bookRepository
        verify(transactionRepository, times(1)).save(any(Transaction.class)); // Ensure save is called on transactionRepository
    }

    @Test
    public void TestBorrowBookFailBookNotFound(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No books found", exception.getReason());

        // Verify find book by id is called once
        verify(bookRepository, times(1)).findById(10L);
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

    @Test
    public void TestReturnBookFailBookNotFound(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(10L));

        // Assert to check exception
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No books found", exception.getReason());

        // Verify find book by id is called once
        verify(bookRepository, times(1)).findById(10L);
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

    @Test
    public void TestBorrowBookFailUserNotFound(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No users found", exception.getReason());

        // Verify find user by name is called once
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction

    }

    @Test
    public void TestReturnBookFailUserNotFound(){
        // set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No users found", exception.getReason());

        // Verify find user by name is called once
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction

    }

    @Test
    public void TestBorrowBookFailAlreadyBorrowed(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), 10L))
                .thenReturn(Optional.of(transaction));  // Existing borrow transaction

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Book already borrowed by you", exception.getReason());

        // Verify
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(bookRepository, times(0)).save(any(Book.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

    @Test
    public void TestReturnBookFailNoBorrowedHistory(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), 10L))
                .thenReturn(Optional.empty());

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.returnBook(book.getBookId()));

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Book is not in borrow with the user", exception.getReason());

        // Verify book entity and transaction entity is updated once
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(bookRepository, times(0)).save(book);
        verify(transactionRepository, times(1)).findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), book.getBookId());
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

    @Test
    public void TestBorrowBookFailNoAvailableCopies(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        book.setCopiesAvailable(0);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findByUser_UserIdAndBook_BookIdAndIsReturnedFalse(user.getUserId(), 10L))
                .thenReturn(Optional.empty());  // No existing borrow history
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Execute
        ResponseStatusException exception = assertThrows( ResponseStatusException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("No copies available", exception.getReason());

        // Verify
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(bookRepository, times(0)).save(any(Book.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

}
