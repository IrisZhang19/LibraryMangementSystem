package com.librarymanagement.project.services;

import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.CategoryResponse;
import com.librarymanagement.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link CategoryService} for managing category-related operations.
 * Provides methods for retrieving, creating, updating, and deleting categories.
 */
@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    public CategoryRepository categoryRepository;

    @Autowired
    public ModelMapper modelMapper;

    /**
     * Retrieves a paginated and sorted list of categories.
     *
     * @param pageNumber The page number to retrieve.
     * @param pageSize The number of categories per page.
     * @param sortBy The field to sort the categories by.
     * @param sortOrder The sorting order (ascending or descending).
     * @return A {@link CategoryResponse} containing paginated category data.
     * @throws ResponseStatusException If a category with the same name already exists.
     */
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Set sort order
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Get paginated categories
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories =categoryPage.getContent();

        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found");
        }

        // Map to CategoryDTOs
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        // Construct and return CategoryResponse
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    /**
     * Creates a new category.
     *
     * @param categoryDTO The data transfer object containing category details.
     * @return The created {@link CategoryDTO}.
     * @throws ResponseStatusException If a category with the same name already exists.
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Check if the new name is valid
        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name must not be empty");
        }

        // Check if a category with the same name already exists
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDb != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name is already in use");
        }

        // Save category
        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


    /**
     * Deletes a category by its ID.
     *
     * @param categoryId The ID of the category to be deleted.
     * @return The deleted {@link CategoryDTO}.
     * @throws ResponseStatusException If the category is not found.
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        // Find  and delete the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found"));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    /**
     * Updates an existing category by its ID.
     *
     * @param categoryId The ID of the category to update.
     * @param categoryDTO The data transfer object containing updated category details.
     * @return The updated {@link CategoryDTO}.
     * @throws ResponseStatusException If the category is not found or name is already in use.
     */
    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        // Find the category by Id
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found"));

        // Check if another category with the same name exists, excluding the current category
        Category existingCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (existingCategory != null && existingCategory.getCategoryName().equals(savedCategory.getCategoryName())) {
            System.out.println("check if name is in use");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name is already in use");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
