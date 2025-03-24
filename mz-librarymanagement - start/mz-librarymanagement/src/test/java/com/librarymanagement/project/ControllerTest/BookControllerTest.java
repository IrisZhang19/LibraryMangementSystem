package com.librarymanagement.project.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.models.*;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.BookResponse;
import com.librarymanagement.project.security.jwt.JwtUtils;
import com.librarymanagement.project.services.BookService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes =MzLibrarymanagementApplication.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private BookService bookService;


    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestCreateBookSuccess() throws Exception {
        String title = "Test Book";
        String author = "Test Author";
        Long bookId = 10L;
        BookDTO bookDTO = new BookDTO();
        BookDTO savedBookDTO = new BookDTO();
        bookDTO.setTitle(title);
        bookDTO.setAuthor(author);
        savedBookDTO.setBookId(bookId);
        savedBookDTO.setTitle(title);
        savedBookDTO.setAuthor(author);
        savedBookDTO.setCategory(new Category(1L, "Category test"));
        when(bookService.addBook(anyLong(), any(BookDTO.class)))
                .thenReturn(savedBookDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories/1/book")
//                .header("Authorization", "Bearer " + jwtToken)  // Attach the JWT token here
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.category.categoryId").value(1L))
                .andExpect((ResultMatcher) jsonPath(".title").value(title))
                .andExpect((ResultMatcher) jsonPath(".author").value(author))
                .andExpect((ResultMatcher) jsonPath(".bookId").value(10));

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestCreateBookFailNotAdmin() throws Exception{
        Book book = new Book();
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories/1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestCreateBookFailCategoryNotFound() throws Exception {
        // set up
        Long categoryId = 100L;
        Book book = new Book();
        when(bookService.addBook(eq(categoryId), any(BookDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No categories found"));

        // execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories/100/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(book)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestCreateBookFailCategoryInvalidRequest() throws Exception {
        // Set up : an invalid book entity with no author and title
        BookDTO bookDTO = new BookDTO();
        when(bookService.addBook(1L, bookDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // Mock and verify
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/admin/categories/1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void TestGetAllBooksSuccess() throws Exception{
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("author");
        bookDTO.setCategory(new Category(1L, "cat"));
        bookDTO.setDescription("  ");
        bookDTO.setCopiesAvailable(5);
        bookDTO.setCopiesTotal(5);
        bookDTO.setBookId(10L);
        List<BookDTO> books = List.of(bookDTO);
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(books);
        bookResponse.setPageNumber(0);
        bookResponse.setPageSize(5);
        bookResponse.setTotalElements(1L);
        bookResponse.setTotalPages(1);
        bookResponse.setLastPage(true);
        when(bookService.getAllBooks(0, 1, "title", "asc"))
                .thenReturn(bookResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/books")
                .param("pageNumber", "0")
                .param("pageSize", "1")
                .param("sortBy", "title")
                .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.content").isArray())
                .andExpect((ResultMatcher) jsonPath("$.content").isNotEmpty())
                .andExpect((ResultMatcher) jsonPath("$.content[0].bookId").value(10))
                .andExpect((ResultMatcher) jsonPath("$.totalPages").value(1))
                .andExpect((ResultMatcher) jsonPath("$.pageNumber").value(0))
                .andExpect((ResultMatcher) jsonPath("$.totalElements").value(1));
    }


    @Test
    public  void TestGetAllBooksFailEmpty() throws Exception {
        BookResponse bookResponse = new BookResponse(new ArrayList<>(), 0, 10, 0L,0, true);
        when(bookService.getAllBooks(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(bookResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'content' : []}"))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateBookSuccess() throws Exception{
        String title = "Test update";
        String author = "Author update";
        Long id = 1L;
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(title);
        bookDTO.setAuthor(author);
        bookDTO.setBookId(1L);
        when(bookService.updateBook(anyLong(), any(BookDTO.class)))
                .thenReturn(bookDTO);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect((ResultMatcher) jsonPath("$.title").value(title))
                .andExpect((ResultMatcher) jsonPath("$.author").value(author))
                .andExpect((ResultMatcher) jsonPath("$.bookId").value(id));

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestUpdateBookFailNotAdmin() throws Exception{
        BookDTO bookDTO = new BookDTO();
        when(bookService.updateBook(anyLong(), any(BookDTO.class)))
                .thenReturn(bookDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateBookFailBookNotFound() throws Exception{
        BookDTO bookDTO = new BookDTO();
        when(bookService.updateBook(anyLong(), any(BookDTO.class)))
                .thenThrow( new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestUpdateBookFailInvalidRequest() throws Exception{
        // Set up : a book without title
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthor("author updated");
        bookDTO.setBookId(1L);
        when(bookService.updateBook(anyLong(), any(BookDTO.class)))
                .thenThrow( new ResponseStatusException(HttpStatus.BAD_REQUEST));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestDeleteBookSuccess() throws Exception{
        BookDTO bookDTO = new BookDTO();
        when(bookService.deleteBook(1L))
                .thenReturn(bookDTO);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/books/{id}", 1L))
                .andExpect(status().isOk());

    }
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestDeleteBookFailNotAdmin() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/books/{id}", 1L))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestDeleteBookFailBookNotFound() throws Exception{
        when(bookService.deleteBook(anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }


}
