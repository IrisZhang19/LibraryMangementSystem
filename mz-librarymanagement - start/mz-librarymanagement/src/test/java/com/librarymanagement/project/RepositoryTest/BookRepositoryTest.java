package com.librarymanagement.project.RepositoryTest;

import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void TestAddBookSuccess(){
        // Given
        String title = "Test Book";
        String author = "Test Author";
        String categoryName = "Test Category";
        Category category = new Category();
        category.setCategoryName(categoryName);
        category = categoryRepository.save(category);  // Save category

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCopiesTotal(10);
        book.setCopiesAvailable(10);
        book.setCategory(category);  // Assign the saved category to the book

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getBookId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo(title);
        assertThat(savedBook.getAuthor()).isEqualTo(author);
        assertThat(savedBook.getCategory()).isNotNull();
        assertThat(savedBook.getCategory().getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    public void TestAddBookFailNoAuthor(){
        // Given
        String title = "Test Book";
        Book book = new Book();
        book.setTitle(title);
        book.setCopiesTotal(10);
        book.setCopiesAvailable(10);

        // When
        assertThrows( ConstraintViolationException.class, () ->
                bookRepository.save(book));

    }

    @Test
    public void TestAddBookFailNoTitle(){
        // Given
        String title = "Test Book";
        String author = "Test Author";
        Book book = new Book();
        book.setAuthor(author);
        book.setCopiesTotal(10);
        book.setCopiesAvailable(10);

        // When
        assertThrows( ConstraintViolationException.class, () ->
                bookRepository.save(book));
    }


    @Test
    public void TestDeleteBookSuccess(){
        // Given
        String title = "Test Book";
        String author = "Test Author";
        String categoryName = "Test Category";
        Category category = new Category();
        category.setCategoryName(categoryName);
        category = categoryRepository.save(category);  // Save category

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCopiesTotal(10);
        book.setCopiesAvailable(10);
        book.setCategory(category);  // Assign the saved category to the book
        Book savedBook = bookRepository.save(book);

        bookRepository.deleteById(savedBook.getBookId());

        // Then
        Optional<Book> deletedBook = bookRepository.findById(book.getBookId());
        assertThat(deletedBook).isEmpty();
    }

    @Test
    public void TestDeleteBookFailNoBookFound(){
        assertDoesNotThrow( () -> bookRepository.deleteById(1L));
    }
    
}
