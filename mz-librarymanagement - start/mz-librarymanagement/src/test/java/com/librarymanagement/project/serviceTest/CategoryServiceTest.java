package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.CategoryResponse;
import com.librarymanagement.project.repositories.BookRepository;
import com.librarymanagement.project.repositories.CategoryRepository;
import com.librarymanagement.project.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class CategoryServiceTest {

    @MockitoBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    public void TestCreateCategorySuccess(){
        //Set up
        String name = "Category 1";
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName(name);
        Category category = new Category();
        category.setCategoryName(name);
        Category savedCategory = new Category(1L, name);

        when(categoryRepository.existsByCategoryNameIgnoreCase(name)).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(savedCategory);

        // Execute
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getCategoryName());
        assertEquals(1L, result.getCategoryId());

        // Verify
        verify(categoryRepository, times(1)).existsByCategoryNameIgnoreCase(name);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void TestCreateCategoryFailNameEmpty(){
        // Set up
        String categoryName = "";
        CategoryDTO categoryDTONew = new CategoryDTO();
        categoryDTONew.setCategoryName(categoryName);

        // Execute and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.createCategory(categoryDTONew);
        });
        assertEquals("Category name must not be empty", exception.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        // Verify
        verify(categoryRepository, never()).save(any());  // Ensure save is never called
    }

    @Test
    public void TestCreateCategoryFailNameExists(){
        // Set up
        String categoryName = "category 1";
        Category categoryNew = new Category();
        categoryNew.setCategoryName(categoryName);
        CategoryDTO categoryDTONew = new CategoryDTO();
        categoryDTONew.setCategoryName(categoryName);
        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(true);

        // Execute and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.createCategory(categoryDTONew);
        });
        assertEquals("Category name is already in use", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        // Verify
        verify(categoryRepository, times(1)).existsByCategoryNameIgnoreCase(categoryName);
        verify(categoryRepository, never()).save(any());  // Ensure save is never called
    }

    @Test
    public void TestGetAllCategoriesSuccess() {
        // Setup
        int pageNumber = 0;
        int pageSize = 2;
        String sortBy = "categoryId";
        String sortOrder = "asc";
        String name1 = "Category 1";
        String name2 = "Category 2";

        // Create sample categories
        Category category1 = new Category(1L, name1);
        Category category2 = new Category(2L, name2);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories);

        // Mock repository behavior
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

        // Execute
        CategoryResponse result = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(name1, result.getContent().get(0).getCategoryName());
        assertEquals(name2, result.getContent().get(1).getCategoryName());
        assertEquals(pageNumber, result.getPageNumber());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLastPage());

        // Verify repository method was called
        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void TestGetAllCategoriesSuccessEmptyCategory() {
        // Set up
        int pageNumber = 0;
        int pageSize = 2;
        String sortBy = "name";
        String sortOrder = "asc";

        Page<Category> emptyPage = Page.empty();

        // Mock repository behavior
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Execute & Assert
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);

        assertNotNull(categoryResponse);
        assertEquals(0, categoryResponse.getContent().size());


        // Verify repository method was called
        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void TestDeleteCategorySuccess() {
        // Set up
        Long categoryId = 1L;
        String name = "Category 1";
        Category category = new Category(categoryId, name);
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, name);

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.existsByCategoryCategoryId(categoryId)).thenReturn(false);
        doNothing().when(categoryRepository).delete(category);  // Mocking void delete method

        // Execute
        CategoryDTO result = categoryService.deleteCategory(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getCategoryName());

        // Verify repository methods were called
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void TestDeleteCategoryCategoryFailNotFound() {
        // Arrange
        Long categoryId = 1L;

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Execute & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertEquals("404 NOT_FOUND \"No categories found\"", exception.getMessage());

        // Verify repository method was called
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any());  // Ensure delete is never called
    }
    @Test
    void TestDeleteCategoryCategoryFailBooksExist() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Category Test");

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(bookRepository.existsByCategoryCategoryId(categoryId)).thenReturn(true);
        // Execute & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Cannot delete category, still books exist under the category", exception.getReason());
        // Verify repository method was called
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(bookRepository, times(1)).existsByCategoryCategoryId(categoryId);
        verify(categoryRepository, never()).delete(any());  // Ensure delete is never called
    }

    @Test
    void TestUpdateCategorySuccess() {
        // Set up
        Long categoryId = 1L;
        String name = "Category 1";
        String updatedName = "Category 1 updated";
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName(updatedName);
        Category savedCategory = new Category(categoryId, name);
        Category updatedCategory = new Category(categoryId, updatedName);

        // Mock repository and model mapper behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        // Execute
        CategoryDTO result = categoryService.updateCategory(categoryId, categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedName, result.getCategoryName());

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(updatedCategory);
//        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    void TestUpdateCategoryFailNameExists() {
        // Set up
        Long categoryId = 1L;
        String name = "Category 1";
        Category existingCategory = new Category(2L, name);
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, name);
        Category savedCategory = new Category(categoryId, name);
        Category updatedCategory = new Category(categoryId, name);

        // Mock repository and model mapper behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryRepository.findByCategoryName(name)).thenReturn(existingCategory);

        // Execute and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.updateCategory(categoryId, categoryDTO);
        });
        assertEquals("Category name is already in use", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        // Verify
        verify(categoryRepository, times(1)).findByCategoryName(name);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void TestUpdateCategoryCategoryNotFound() {
        // Set up
        Long categoryId = 1L;
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, "Updated Category");
        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Execute & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoryService.updateCategory(categoryId, categoryDTO);
        });

        assertEquals("No categories found", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        // Verify
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any());  // Ensure save is never called
    }

}
