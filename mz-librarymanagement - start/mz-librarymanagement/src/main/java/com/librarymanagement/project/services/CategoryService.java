package com.librarymanagement.project.services;


import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.CategoryResponse;

/**
 * Service interface for managing Category related operations.
 * Defines methods for creating, updating, deleting, and retrieving categories.
 */
public interface CategoryService {

    /**
     * Retrieves a paginated and sorted list of categories.
     *
     * @param pageNumber The page number to retrieve.
     * @param pageSize The number of categories per page.
     * @param sortBy The field to sort the categories by.
     * @param sortOrder The sorting order (ascending or descending).
     * @return A {@link CategoryResponse} containing paginated category data.
     */
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    /**
     * Creates a new category.
     *
     * @param categoryDTO The data transfer object containing category details.
     * @return The created {@link CategoryDTO}.
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId The ID of the category to be deleted.
     * @return The deleted {@link CategoryDTO}.
     */
    CategoryDTO deleteCategory(Long categoryId);

    /**
     * Updates an existing category by its ID.
     *
     * @param categoryId The ID of the category to update.
     * @param categoryDTO The data transfer object containing updated category details.
     * @return The updated {@link CategoryDTO}.
     */
    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
}
