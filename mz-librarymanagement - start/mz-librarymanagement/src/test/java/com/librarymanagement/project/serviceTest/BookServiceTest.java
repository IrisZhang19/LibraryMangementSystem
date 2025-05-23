package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.exceptions.BusinessException;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.exceptions.ValidationException;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import com.librarymanagement.project.services.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Long categoryId;
    private String categoryName;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryId = 1L;
        categoryName = "category 1";
        category = new Category(categoryId, categoryName);
    }

    @Test
    public void TestCreateBookSuccess(){
        //Set up
        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(title);
        bookDTO.setAuthor(author);
        bookDTO.setCategory(category);
        bookDTO.setCopiesTotal(10);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCopiesTotal(10);
        book.setCategory(category);

        Book savedBook = new Book(bookId, title, author, 10, 10, 0, true, "", category);
        BookDTO savedBookDTO = new BookDTO(bookId, title, author, 10, 10, 0, true, "", category);


        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(savedBookDTO);


        // execute
        BookDTO result = bookService.addBook(categoryId, bookDTO);

        // assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(bookId, result.getBookId());
        assertEquals(category, result.getCategory());
        assertEquals(10, result.getCopiesTotal());
        assertEquals(10, result.getCopiesAvailable());
        assertEquals(0, result.getCopiesBorrowed());
        assertTrue(result.isActive());

        //verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void TestCreateBookFailInvalidRequestBody(){
        //Set up
        String author = "Author 1";
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthor(author);
        bookDTO.setCategory(category);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // execute
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookService.addBook(categoryId, bookDTO) );

        // assert
        assertEquals("Book title must not be empty", exception.getMessage());

        //Verify book never saved to repository
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestCreateBookFailTitleEmpty(){
        //Set up
        String author = "Author 1";
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthor(author);
        bookDTO.setCategory(category);
        bookDTO.setTitle("");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // execute
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookService.addBook(categoryId, bookDTO) );

        // assert
        assertEquals("Book title must not be empty", exception.getMessage());

        //Verify book never saved to repository
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestCreateBookFailNoTotalCopies(){
        //Set up
        String author = "Author Test";
        String title = "Book Test";
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthor(author);
        bookDTO.setCategory(category);
        bookDTO.setTitle(title);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // execute
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookService.addBook(categoryId, bookDTO) );

        // assert
        assertEquals("Total copies must be provided and more than 0", exception.getMessage());

        //Verify book never saved to repository
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestCreateBookFailCategoryNotFound(){
        // Set up
        Long categoryId = 1L;
        BookDTO bookDTO = new BookDTO();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Execute & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.addBook(categoryId, bookDTO);
        });

        assertEquals("No categories found by category id: " + categoryId, exception.getMessage());

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    public void TestGetAllBookSuccess(){
        //Set up
        int pageNumber = 0;
        int pageSize = 2;
        String sortBy = "bookId";
        String sortOrder = "asc";
        Long categoryId = 1L;
        String  categoryName = "category 1";
        Category category = new Category(categoryId, categoryName);

        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        Book book = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books);
        BookDTO bookDTO = new BookDTO(bookId, title, author, 10, 8, 2, true, "", category);

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
        // execute
        BookResponse result = bookService.getAllBooks(pageNumber, pageSize, sortBy, sortOrder);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(bookId, result.getContent().get(0).getBookId());
        assertEquals(title, result.getContent().get(0).getTitle());
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLastPage());

        // Verify repository method is called to get all the books
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void TestUpdateBookSuccess(){
        //Set up
        Long categoryId = 1L;
        String  categoryName = "category 1";
        Category category = new Category(categoryId, categoryName);

        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        String newTitle = "Book Updated";
        Book bookFromDB = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        Book savedBook = new Book(bookId, newTitle, author, 10, 8, 2, true, "", category);
        BookDTO bookDTO = new BookDTO(bookId, newTitle, author, 10, 8, 2, true, "", category);
        Book newBook = new Book(bookId, newTitle, author, 10, 8, 2, true, "", category);

        when(modelMapper.map(bookDTO, Book.class)).thenReturn(newBook);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookFromDB));
        when(bookRepository.save(bookFromDB)).thenReturn(savedBook);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(bookDTO);

        // execute
        BookDTO result = bookService.updateBook(bookId, bookDTO);

        // assert
        assertNotNull(result);
        assertEquals(newTitle, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(bookId, result.getBookId());
        assertEquals(category, result.getCategory());

        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(bookFromDB);
    }

    @Test
    public void TestPartialUpdateBookSuccess(){
        //Set up
        Long categoryId = 1L;
        String  categoryName = "category 1";
        Category category = new Category(categoryId, categoryName);

        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        String newTitle = "Book Updated";
        Book bookFromDB = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        Book savedBook = new Book(bookId, newTitle, author, 10, 8, 2, true, "", category);
        BookDTO savedBookDTO = new BookDTO(bookId, newTitle, author, 10, 8, 2, true, "", category);

        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(bookId);
        bookDTO.setTitle(newTitle);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookFromDB));
        when(bookRepository.save(bookFromDB)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(savedBookDTO);
        // execute
        BookDTO result = bookService.partialUpdateBook(bookId, bookDTO);

        // assert
        assertNotNull(result);
        assertEquals(newTitle, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(bookId, result.getBookId());
        assertEquals(category, result.getCategory());

        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(bookFromDB);
    }

    @Test
    public void TestUpdateBookFailBookNotFound(){
        //Set up
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // execute and assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookService.updateBook(bookId, bookDTO));
        assertEquals("No books found", exception.getMessage());

        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestPartialUpdateBookFailBookNotFound(){
        //Set up
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // execute and assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookService.partialUpdateBook(bookId, bookDTO));
        assertEquals("No books found by id : "  + bookId, exception.getMessage());

        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestUpdateBookFailInactiveBook(){
        //Set up
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";
        String newTitle = "Book 1 updated";
        Book book = new Book(bookId, title, author, 10, 8, 2, false, "", category);
        BookDTO bookDTO = new BookDTO(bookId, newTitle, author, 10, 8, 2, false, "", category);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Execute and Assert
        BusinessException exception = assertThrows( BusinessException.class, () ->
                bookService.updateBook(bookId, bookDTO));
        assertEquals("Cannot update inactive book", exception.getMessage());

        // Verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestPartialUpdateBookFailInactiveBook(){
        //Set up
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";
        String newTitle = "Book 1 updated";
        Book book = new Book(bookId, title, author, 10, 8, 2, false, "", category);
        BookDTO bookDTO = new BookDTO(bookId, newTitle, author, 10, 8, 2, false, "", category);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Execute and Assert
        BusinessException exception = assertThrows( BusinessException.class, () ->
                bookService.partialUpdateBook(bookId, bookDTO));
        assertEquals("Cannot update inactive book", exception.getMessage());

        // Verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestUpdateBookFailBookInvalidRequest(){
        //Set up
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";

        Book book = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        BookDTO bookDTO = new BookDTO(bookId, "", author, 10, 8, 2, true, "", category);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // execute
        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookService.updateBook(bookId, bookDTO));

        // assert
        assertEquals("Book title must not be empty", exception.getMessage());

        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void TestDeleteBookSuccess(){
        //Set up
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";
        Book book = new Book(bookId, title, author, 10, 10, 0, true, "", category);
        Book savedBook = new Book(bookId, title, author, 10, 10, 0, false, "", category);
        BookDTO savedBookDTO = new BookDTO(bookId, title, author, 10, 10, 0, false, "", category);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(savedBook)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(savedBookDTO);

        // execute
        BookDTO result = bookService.deleteBook(bookId);

        // assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(author, result.getAuthor());
        assertEquals(bookId, result.getBookId());
        assertEquals(category, result.getCategory());
        assertFalse(result.isActive());
        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void TestDeleteBookFailNoBookFound(){
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                bookService.deleteBook(bookId));

        assertEquals("No books found by book id :"  + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    public void TestDeleteBookSuccessUnreturnedCopies(){
        //Set up
        Long bookId = 10L;
        String title = "Book 1";
        String author = "Author 1";
        Book book = new Book(bookId, title, author, 10, 8, 2, true, "", category);
        Book savedBook = new Book(bookId, title, author, 10, 8, 2, false, "", category);
        BookDTO savedBookDTO = new BookDTO(bookId, title, author, 10, 8, 2, false, "", category);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(savedBook)).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(savedBookDTO);

        // execute
        BookDTO result =  bookService.deleteBook(bookId);

        // assert
        assertFalse(result.isActive());
        //verify
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
        verify(bookRepository, never()).deleteById(bookId);
    }

}
