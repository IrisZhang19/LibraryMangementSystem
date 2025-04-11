package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.exceptions.BusinessException;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.models.*;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.TransactionDTO;
import com.librarymanagement.project.payloads.UserDTO;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.TransactionRepository;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import com.librarymanagement.project.services.BorrowReturnServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BorrowReturnServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BorrowReturnServiceImpl borrowReturnService;

    private Book book;
    private Book bookAfterBorrow;
    private Book bookAfterReturn;
    private BookDTO bookDTO;
    private User user;
    private UserDTO userDTO;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private Set<GrantedAuthority> authorities;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks

        // set up a user
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(10, AppRole.ROLE_USER));  // Role entity
        String userName = "Test User";
        String userEmail = "user@test.com";
        user = new User(1L, userName, "password1", userEmail, roles);
        userDTO = new UserDTO(1L, userName, userEmail);
        // Convert Set<Role> to Set<GrantedAuthority> for security context
        authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toSet());

        // set up book
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";
        Category category = new Category(1L, "Category Test");
        CategoryDTO categoryDTO = new CategoryDTO(1L, "Category Test");
        book = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        bookAfterBorrow = new Book(bookId, title, author, 10, 7, 3, true, "", category);
        bookAfterReturn = new Book(bookId, title, author, 10, 9, 1, true, "", category);
        bookDTO = new BookDTO(bookId, title, author, 10, 8, 2, true, "", category);
        BookDTO bookDTOAfterBorrow = new BookDTO(bookId, title, author, 10, 7, 3, true, "", category);

        // setup up transaction
        LocalDate borrowTime = LocalDate.now();
        transaction = new Transaction();
        transaction.setBorrowedDate(borrowTime);
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setTransactionId(1L);
        transaction.setReturned(false);
        transactionDTO = new TransactionDTO(1L, borrowTime, null, false, bookDTOAfterBorrow ,userDTO);
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
        when(modelMapper.map(transaction, TransactionDTO.class)).thenReturn(transactionDTO);
        when(bookRepository.save(any(Book.class))).thenReturn(bookAfterBorrow);

        // Execute
        TransactionDTO result = borrowReturnService.borrowBook(10L);

        // Assert to check the fields with desired values
        assertNotNull(result);
        assertEquals(result.getUser().getUserId(), user.getUserId());
        assertEquals(result.getBorrowedDate(), transaction.getBorrowedDate());
        assertNull(result.getReturnedDate());
        assertFalse(result.isReturned());
        assertEquals(result.getBook().getBookId(), book.getBookId());
        assertEquals(7, book.getCopiesAvailable());  // Available copies should decrease by 1
        assertEquals(3, book.getCopiesBorrowed());

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
        transactionDTO.setReturnedDate(returnDate);
        transaction.setReturned(true);
        transactionDTO.setReturned(true);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(transaction, TransactionDTO.class)).thenReturn(transactionDTO);

        // Execute
        TransactionDTO result = borrowReturnService.returnBook(10L);

        // Assertions
        assertNotNull(result);
        assertEquals(result.getUser().getUserId(), user.getUserId());
        assertEquals(result.getBorrowedDate(), transaction.getBorrowedDate());
//        assertEquals(result.getReturnedDate(), returnDate);
        assertNotNull(result.getReturnedDate());
        assertTrue(result.isReturned());
        assertEquals(result.getBook().getBookId(), book.getBookId());
        assertEquals(9, book.getCopiesAvailable()); // Available copies should increase by 1
        assertEquals(1, book.getCopiesBorrowed());

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
        ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals("No books found by book id : " + book.getBookId(), exception.getMessage());

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
        ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () ->
                borrowReturnService.borrowBook(10L));

        // Assert to check exception
        assertEquals("No books found by book id : " + book.getBookId(), exception.getMessage());

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
        ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals("No user found by username : " + user.getUserName(), exception.getMessage());

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
       ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert to check exception
        assertEquals("No user found by username : " + user.getUserName(), exception.getMessage());

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
        BusinessException exception = assertThrows( BusinessException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert
        assertEquals("Book already borrowed by you", exception.getMessage());

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
        BusinessException exception = assertThrows( BusinessException.class, () ->
                borrowReturnService.returnBook(book.getBookId()));

        // Assertions
        assertEquals("Book is not in borrow with the user", exception.getMessage());

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

        // Execute
        BusinessException exception = assertThrows( BusinessException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));

        // Assert
        assertEquals("No copies available for this book", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(bookRepository, never()).save(any(Book.class));
        verify(transactionRepository, never()).save(any(Transaction.class)); // Ensure save is not called for transaction
    }

    @Test
    public void TestBorrowBookFailInactive(){
        // Set up
        UserDetails userDetails = new UserDetailsImpl(1L, user.getUserName(),  user.getEmail(), user.getPassword(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        book.setActive(false);
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        // Execute and assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
                borrowReturnService.borrowBook(book.getBookId()));
        assertEquals("Book can no longer be borrowed", exception.getMessage());

        // Verify
        verify(bookRepository, never()).save(book);  // Ensure save is never called on bookRepository
        verify(transactionRepository, never()).save(any(Transaction.class)); // Ensure save is never called on transactionRepository

    }
}
