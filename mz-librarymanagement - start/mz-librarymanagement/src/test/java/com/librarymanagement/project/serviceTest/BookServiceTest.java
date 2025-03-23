package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import com.librarymanagement.project.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class BookServiceTest {

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private ModelMapper modelMapper;


    @Autowired
    private BookService bookService;


    @BeforeEach
    void setUp() {
        Long categoryId = 1L;
        String  categoryName = "category 1";
        Category category = new Category(categoryId, categoryName);
    }

    @Test
    public void TestCreateBookSuccess(){
        //Set up
        Long categoryId = 1L;
        String  categoryName = "category 1";
        Category category = new Category(categoryId, categoryName);

        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        BookDTO bookDTO = new BookDTO(title, author);
        Book book = new Book(bookId, title, author);
        Book savedBook = new Book(bookId, title, author, category);
        BookDTO savedBookDTO = new BookDTO(bookId, title, author, category);

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

        //verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, times(1)).save(book);
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    public void TestCreateBookFail(){
        // Set up
        Long categoryId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        BookDTO bookDTO = new BookDTO(title, author);


        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Execute & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.addBook(categoryId, bookDTO);
        });

        assertEquals("category not exist", exception.getMessage());

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, never()).save(any());
    }


}
