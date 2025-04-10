package com.librarymanagement.project.repositories;

import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Book} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations and
 * pagination support for the {@link Book} entity. Custom query methods are defined
 * to retrieve books based on category, author, and title.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

    /**
     * Check if any book exist under given category.
     *
     * @param categoryId The id of the category to be checked.
     * @return true if there are books exist under this category false otherwise.
     */
    boolean existsByCategoryCategoryId(Long categoryId);

    /**
     * Finds books by their category with pagination support.
     *
     * @param category The category of the books to retrieve.
     * @param pageDetails The pagination details (e.g., page number and size).
     * @return A {@link Page} of books that belong to the specified category.
     */
    Page<Book> findByCategory(Category category, Pageable pageDetails);

    /**
     * Finds books by their author name, allowing case-insensitive partial matches.
     * The search term will match any part of the author's name.
     *
     * @param author The author name or partial name to search for.
     * @param pageDetails The pagination details (e.g., page number and size).
     * @return A {@link Page} of books whose author matches the search term.
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageDetails);

    /**
     * Finds books by their title, allowing case-insensitive partial matches.
     * The search term will match any part of the book's title.
     *
     * @param s The title or partial title to search for.
     * @param pageDetails The pagination details (e.g., page number and size).
     * @return A {@link Page} of books whose title matches the search term.
     */
    Page<Book> findByTitleLikeIgnoreCase(String s, Pageable pageDetails);
}
