package com.librarymanagement.project.ControllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.security.jwt.MessageResponse;
import com.librarymanagement.project.security.jwt.SigninRequest;
import com.librarymanagement.project.security.jwt.SignupRequest;
import com.librarymanagement.project.security.jwt.UserInfoResponse;
import com.librarymanagement.project.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes =MzLibrarymanagementApplication.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


    @Test
    public void TestSignInUserSuccess() throws Exception {
        // Set up :  user credentials
        String password = "password";
        String username = "testuser";
        String token = "valid jwt Token";
        SigninRequest signInRequest = new SigninRequest();
        signInRequest.setPassword(password);
        signInRequest.setUsername(username);
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L,
                username, List.of("USER"), token);
        when(authService.signin(any(SigninRequest.class)))
                .thenReturn(userInfoResponse);

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.username").value(username))
                .andExpect((ResultMatcher) jsonPath("$.password").doesNotHaveJsonPath())
                .andExpect((ResultMatcher) jsonPath("$.roles").isArray())
                .andExpect((ResultMatcher) jsonPath("$.roles.length()").value(1))
                .andExpect((ResultMatcher) jsonPath("$.roles[0]").value("USER"))
                .andExpect((ResultMatcher) jsonPath("$.jwtToken").value(token));
    }

    @Test
    public void TestSignInUserSuccessAdmin() throws Exception {
        // Set up :  user credentials
        String password = "password";
        String username = "testuser";
        String token = "valid jwt Token";
        SigninRequest signInRequest = new SigninRequest();
        signInRequest.setPassword(password);
        signInRequest.setUsername(username);
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L,
                username, List.of("USER", "ADMIN"), token);
        when(authService.signin(any(SigninRequest.class)))
                .thenReturn(userInfoResponse);
        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.username").value(username))
                .andExpect((ResultMatcher) jsonPath("$.password").doesNotHaveJsonPath())
                .andExpect((ResultMatcher) jsonPath("$.roles").isArray())
                .andExpect((ResultMatcher) jsonPath("$.roles.length()").value(2))
                .andExpect((ResultMatcher) jsonPath("$.roles", hasItem("USER")))
                .andExpect((ResultMatcher) jsonPath("$.roles", hasItem("ADMIN")))
                .andExpect((ResultMatcher) jsonPath("$.jwtToken").value(token));
    }

    @Test
    public void TestSigninFail() throws Exception {
        // Set up :  user credentials
        SigninRequest signInRequest = new SigninRequest();
        when(authService.signin(any(SigninRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInRequest)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void TestSignupUserSuccess() throws Exception {
        // Set up : user credentials
        String password = "password";
        String username = "testuser";
        String email = "tet@emai.com";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        when(authService.signupUser(signupRequest))
                .thenReturn(new MessageResponse("User registered successfully!"));

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())  // Expecting 201 Created status
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void TestSignupUserSuccessAdmin() throws Exception {
        // Set up : user credentials
        String password = "password";
        String username = "testuser";
        String email = "tet@email.com";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        when(authService.signupAdmin(signupRequest))
                .thenReturn(new MessageResponse("User registered successfully!"));

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())  // Expecting 201 Created status
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void TestSignupUserFailNotAdmin() throws Exception {
        // Set up : user credentials
        String password = "password";
        String username = "testuser";
        String email = "tet@emai.com";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);

        // Execute and verify
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
