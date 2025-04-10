package com.librarymanagement.project.repositories;


import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Category} entities.
 * This interface extends {@link JpaRepository} to provide basic CRUD operations and
 * pagination support for the {@link Category} entity. Custom query methods are defined
 * to retrieve category by name.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name.
     *
     * @param categoryName The name of the category.
     * @return The Category entity found, if not null.
     */
    Category findByCategoryName(String categoryName);

    /**
     * Check if any category has this given categoryName.
     *
     * @param categoryName The name to be checked.
     * @return True if the name is already in use false otherwise.
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
