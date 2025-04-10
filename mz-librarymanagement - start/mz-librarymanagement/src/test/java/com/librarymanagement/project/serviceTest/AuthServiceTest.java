package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.TestConfig;
import com.librarymanagement.project.exceptions.BusinessException;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.models.AppRole;
import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.RoleRepository;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.jwt.*;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import com.librarymanagement.project.services.AuthService;
import com.librarymanagement.project.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
@Import(TestConfig.class)
public class AuthServiceTest {

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private  Authentication authentication;

//    @Autowired
//    @Mock
    @MockitoBean
    private RoleRepository roleRepository;
//
    @MockitoBean
//    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;


    @Test
    public void TestSignInSuccess(){
        // Set up
        String password = "password";
        String username = "testuser";
        String email = "test@user.com";
        String token = "valid jwt Token";
        Long userId = 1L;
        SigninRequest signinRequest = new SigninRequest(username, password);
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(AppRole.ROLE_USER.name()));
        UserDetails userDetails = new UserDetailsImpl(userId, username, email, password, authorities);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateTokenFromUsername(any(UserDetailsImpl.class))).thenReturn(token);

        // Execute
        UserInfoResponse userInfoResponse = authService.signin(signinRequest);

        // Check the response fields
        assertEquals(token, userInfoResponse.getJwtToken());
        assertEquals(userId, userInfoResponse.getId());
        assertEquals(username, userInfoResponse.getUsername());
        assertEquals( 1,userInfoResponse.getRoles().size());
        assertEquals(AppRole.ROLE_USER.toString(), userInfoResponse.getRoles().get(0));

        // Verify method calls
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void TestSignInFailBadCredentials(){
        // Set up
        String password = "password";
        String username = "testuser";
        SigninRequest signinRequest = new SigninRequest(username, password);

        // Execute
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(
          new BadCredentialsException("Bad credentials")
        );

        // Execute
        assertThrows(BadCredentialsException.class, () ->
                authService.signin(signinRequest));

        // Verify method calls
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void TestSignupUserSuccess(){
        // Set up
        String password = "password";
        String username = "testuser";
        String email = "test@user.com";
        String token = "valid jwt Token";
        Role role = new Role(10, AppRole.ROLE_USER);
        Long userId = 1L;
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(any(User.class));


        // Execute
        MessageResponse messageResponse = authService.signupUser(signupRequest);

        // Check the response fields
        assertEquals("User registered successfully!", messageResponse.getMessage());

        // Verify method calls
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public  void TestSignupUserFailRepeatedUsername(){
        String password = "password";
        String username = "testuser";
        String email = "test@user.com";

        SignupRequest signupRequest = new SignupRequest(username, email, password);
        when(userRepository.existsByUserName(username)).thenReturn(true);

        // Execute
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signupUser(signupRequest));

        // Check the response fields
        assertEquals("Error: Username " +  username + " is already taken!", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        // web security config initialize two users by default
    }

    @Test
    public  void TestSignupUserFailRepeatedEmail(){
        String password = "password";
        String username = "testuser";
        String email = "test@user.com";

        SignupRequest signupRequest = new SignupRequest(username, email, password);
        when(userRepository.existsByUserName(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Execute
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signupUser(signupRequest));

        // Check the response fields
        assertEquals("Error: This Email " + email +  " is already registered!", exception.getMessage());

        // Verify method calls
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public  void TestSignupUserFailRoleNotExist(){
        String password = "password";
        String username = "testuser";
        String email = "test@user.com";

        SignupRequest signupRequest = new SignupRequest(username, email, password);
        when(userRepository.existsByUserName(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.empty());

        // Execute
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                authService.signupUser(signupRequest));

        // Check the response fields
        assertEquals("Role User not found", exception.getMessage());

        // Verify method calls
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    public void TestSignupAdminSuccess(){
        // Set up
        String password = "password";
        String username = "testadmin";
        String email = "test@admin.com";
        Role role = new Role(10, AppRole.ROLE_ADMIN);
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.ROLE_ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(any(User.class));

        // Execute
        MessageResponse messageResponse = authService.signupAdmin(signupRequest);

        // Check the response fields
        assertEquals("Admin registered successfully!", messageResponse.getMessage());

        // Verify method calls
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void TestSignupAdminFailRepeatedName(){
        // Set up singup request
        String password = "password";
        String username = "testadmin";
        String email = "test@admin.com";
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        // Mock repository call
        when(userRepository.existsByUserName(anyString())).thenReturn(true);

        // Execute
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signupAdmin(signupRequest));

        // Check the exception
        assertEquals("Error: Username " + username + " is already taken!", exception.getMessage());

        // Verify method save never been called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void TestSignupAdminFailRepeatedEmail(){
        // Set up
        String password = "password";
        String username = "testadmin";
        String email = "test@admin.com";
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Execute
        BusinessException exception = assertThrows(BusinessException.class, () ->
                authService.signupAdmin(signupRequest));

        // Check the exception
        assertEquals("Error: This Email " + email + " is already registered!", exception.getMessage());

        // Verify method save never been called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void TestSignupAdminFailRoleNotFound(){
        // Set up signup request
        String password = "password";
        String username = "testadmin";
        String email = "test@admin.com";
        SignupRequest signupRequest = new SignupRequest(username, email, password);

        // Mock repository calls
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByRoleName(AppRole.ROLE_ADMIN)).thenReturn(Optional.empty());
        // Execute
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                authService.signupAdmin(signupRequest));

        // Check the exception
        assertEquals("Role Admin not found", exception.getMessage());

        // Verify method save never been called
        verify(userRepository, never()).save(any(User.class));
    }

}
