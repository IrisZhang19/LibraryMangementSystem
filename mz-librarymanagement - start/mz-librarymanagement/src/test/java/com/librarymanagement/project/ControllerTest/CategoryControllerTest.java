package com.librarymanagement.project.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.Book;
import com.librarymanagement.project.models.Category;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.payloads.CategoryDTO;
import com.librarymanagement.project.payloads.CategoryResponse;
import com.librarymanagement.project.security.jwt.JwtUtils;
import com.librarymanagement.project.services.BookService;
import com.librarymanagement.project.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestCreateCategorySuccess() throws Exception {
        // Set up category
        Long categoryId = 1L;
        String categoryName = "Category test";
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName(categoryName);
        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setCategoryName(categoryName);
        savedCategoryDTO.setCategoryId(categoryId);

        when(categoryService.createCategory(categoryDTO)).thenReturn(savedCategoryDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories")
//                .header("Authorization", "Bearer " + jwtToken)  // Attach the JWT token here
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.categoryId").value(categoryId))
                .andExpect((ResultMatcher) jsonPath("$.categoryName").value(categoryName));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestCreateCategoryFailNotAdmin() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestCreateCategoryFailCategoryNull() throws Exception {
        // Set up : an invalid category entity with name
        CategoryDTO categoryDTO = new CategoryDTO();
        when(categoryService.createCategory(categoryDTO)).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name must not be empty")
        );

        // Mock and verify
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public  void TestGetAllCategoriesSuccessEmpty() throws Exception {
        CategoryResponse categoryResponse = new CategoryResponse(new ArrayList<>(), 0, 10, 0L,0, true);
        when(categoryService.getAllCategories(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(categoryResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'content' : []}"))
                .andReturn();
    }

    @Test
    public void TestGetAllCategoriesSuccess() throws Exception{
        String name1 = "Test Cat 1";
        String name2 = "Test Cat 2";
        CategoryDTO categoryDTO1 = new CategoryDTO(1L, name1);
        CategoryDTO categoryDTO2 = new CategoryDTO(2L, name2);
        List<CategoryDTO> categoryDTOS = List.of(categoryDTO1, categoryDTO2);
        CategoryResponse categoryResponse = new CategoryResponse(categoryDTOS, 0, 1, 2L, 1, true );
        when(categoryService.getAllCategories(0, 1, "categoryId", "asc"))
                .thenReturn(categoryResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/categories")
                        .param("pageNumber", "0")
                        .param("pageSize", "1")
                        .param("sortBy", "categoryId")
                        .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.content").isArray())
                .andExpect((ResultMatcher) jsonPath("$.content.length()").value(2))
                .andExpect((ResultMatcher) jsonPath("$.content[0].categoryId").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.content[1].categoryId").value(2L))
                .andExpect((ResultMatcher) jsonPath("$.totalPages").value(1))
                .andExpect((ResultMatcher) jsonPath("$.pageNumber").value(0))
                .andExpect((ResultMatcher) jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateCategorySuccess() throws Exception{
        // Set up category
        Long categoryId = 1L;
        String categoryName = "Category test";
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName(categoryName);
        categoryDTO.setCategoryId(categoryId);

        when(categoryService.updateCategory(anyLong(), any(CategoryDTO.class)))
                .thenReturn(categoryDTO);
        // Run and check result
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect((ResultMatcher) jsonPath("$.categoryId").value(categoryId))
                .andExpect((ResultMatcher) jsonPath("$.categoryName").value(categoryName));

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestUpdateCategoryFailNotAdmin() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateCategoriesFailCategoryNotFound() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        when(categoryService.updateCategory(anyLong(), any(CategoryDTO.class)))
                .thenThrow( new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateCategoryFailInvalidRequest() throws Exception{
        // Set up : a category with null name
        CategoryDTO categoryDTO = new CategoryDTO();
        when(categoryService.updateCategory(anyLong(), any(CategoryDTO.class)))
                .thenThrow( new ResponseStatusException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestDeleteCategorySuccess() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        when(categoryService.deleteCategory(1L))
                .thenReturn(categoryDTO);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/categories/{id}", 1L))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestDeleteCategoryFailNotAdmin() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        when(categoryService.deleteCategory(1L))
                .thenReturn(categoryDTO);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/categories/{id}", 1L))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestDeleteCategoryFailNotFound() throws Exception{
        when(categoryService.deleteCategory(anyLong()))
                .thenThrow( new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/categories/{id}", 1L))
                .andExpect(status().isNotFound());
    }

}
