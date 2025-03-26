package com.librarymanagement.project.controllers;


import com.librarymanagement.project.configs.AppConstants;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.CategoryResponse;
import com.librarymanagement.project.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling category-related API requests.
 * Provides endpoints for getting all categories, adding, updating, and deleting categories.
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    public CategoryService categoryService;

    /**
     * Retrieves a paginated list of categories, sorted by a specified field and order.
     *
     * @param pageNumber the page number to retrieve (default is AppConstants.PAGE_NUMBER)
     * @param pageSize the number of categories per page (default is AppConstants.PAGE_SIZE)
     * @param sortBy the field by which to sort categories (default is AppConstants.SORT_CATEGORIES_BY)
     * @param sortOrder the order in which to sort categories (default is AppConstants.SORT_DIR)
     * @return a ResponseEntity containing a CategoryResponse with category details
     */
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name="sortOrDER", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    /**
     * Creates a new category in the system.
     *
     * @param categoryDTO the category data to be added
     * @return a ResponseEntity containing the created CategoryDTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid  @RequestBody CategoryDTO categoryDTO){
        CategoryDTO returnedCate =   categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(returnedCate, HttpStatus.CREATED);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param categoryId the ID of the category to delete
     * @return a ResponseEntity containing the deleted CategoryDTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * Updates an existing category by its ID.
     *
     * @param categoryId the ID of the category to update
     * @param categoryDTO the updated category data
     * @return a ResponseEntity containing the updated CategoryDTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }
}
