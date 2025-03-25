package com.librarymanagement.project.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.payloads.BookDTO;
import com.librarymanagement.project.payloads.TranscationDTO;
import com.librarymanagement.project.payloads.UserDTO;
import com.librarymanagement.project.services.BorrowReturnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class BorrowReturnControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BorrowReturnService borrowReturnService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestBorrowBookSuccessByUser() throws Exception {
        // Set up
        Long bookId = 10L;
        LocalDate borrowTime = LocalDate.now();
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(bookId);
        TranscationDTO transcationDTO = new TranscationDTO();
        transcationDTO.setBook(bookDTO);
        transcationDTO.setBorrowedDate(borrowTime);
        transcationDTO.setReturned(false);
        transcationDTO.setTransactionId(100L);
        transcationDTO.setUser(new UserDTO(100L, "user", "test@email.com"));
        when(borrowReturnService.borrowBook(10L)).thenReturn(transcationDTO);

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect((ResultMatcher) jsonPath("$.transactionId").value(100))
                .andExpect((ResultMatcher) jsonPath("$.user.userName").value("user"))
                .andExpect((ResultMatcher) jsonPath("$.book.bookId").value(10))
                .andExpect((ResultMatcher) jsonPath("$.borrowedDate").value(borrowTime.toString()))
                .andExpect((ResultMatcher) jsonPath("$.returnedDate", nullValue()));

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestBorrowBookFailNotUserButAdmin() throws Exception {
        // Admin is not allowed to borrow books
        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void TestBorrowBookFailNotLogIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestBorrowBookFailNoUserFound() throws Exception {
        // Set up mock service response
        when(borrowReturnService.borrowBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found"));

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestBorrowBookFailNoBookFound() throws  Exception {
        when(borrowReturnService.borrowBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestBorrowBookFailBorrowAlready() throws Exception {
        when(borrowReturnService.borrowBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book already borrowed by you"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestBorrowBookFailNoAvailableCopies() throws Exception {
        when(borrowReturnService.borrowBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No copies available"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestReturnBookSuccessByUser() throws Exception {
        // Set up
        Long bookId = 10L;
        LocalDate borrowTime = LocalDate.of(2024, 01,01);
        LocalDate returnTime = LocalDate.now();
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(bookId);
        TranscationDTO transcationDTO = new TranscationDTO();
        transcationDTO.setBook(bookDTO);
        transcationDTO.setBorrowedDate(borrowTime);
        transcationDTO.setReturnedDate(returnTime);
        transcationDTO.setReturned(true);
        transcationDTO.setTransactionId(100L);
        transcationDTO.setUser(new UserDTO(100L, "user", "test@email.com"));
        when(borrowReturnService.returnBook(10L)).thenReturn(transcationDTO);

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/return/10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect((ResultMatcher) jsonPath("$.transactionId").value(100))
                .andExpect((ResultMatcher) jsonPath("$.user.userName").value("user"))
                .andExpect((ResultMatcher) jsonPath("$.book.bookId").value(10))
                .andExpect((ResultMatcher) jsonPath("$.borrowedDate").value(borrowTime.toString()))
                .andExpect((ResultMatcher) jsonPath("$.returnedDate").value(returnTime.toString()));

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestBorrowReturnFailNotUserButAdmin() throws Exception {
        // Admin is not allowed to borrow books
        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/return/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void TestReturnBookFailNotLogIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/return/10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestReturnBookFailNoBookFound() throws  Exception {
        when(borrowReturnService.returnBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/return/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestReturnBookFailNotBorrowed() throws Exception {
        when(borrowReturnService.returnBook(10L))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Book is not in borrow with the user"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/return/10"))
                .andExpect(status().isForbidden());
    }
}
