package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import com.librarymanagement.project.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

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
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, categoryName);

        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(title);
        bookDTO.setAuthor(author);
        bookDTO.setCategory(category);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);

        Book savedBook = new Book();
        savedBook.setBookId(bookId);
        savedBook.setTitle(title);
        savedBook.setAuthor(author);
        savedBook.setCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(savedBook);

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
    }

    @Test
    public void TestCreateBookFailCategoryNotFound(){
        // Set up
        Long categoryId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(title);
        bookDTO.setAuthor(author);


        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Execute & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookService.addBook(categoryId, bookDTO);
        });

        assertEquals("No categories found", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, never()).save(any());
    }


}
