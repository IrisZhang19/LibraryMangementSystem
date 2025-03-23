package com.librarymanagement.project.repositories;


import com.librarymanagement.project.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Category entities in the database.
 * Extends JpaRepository to provide CRUD operations.
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
}
