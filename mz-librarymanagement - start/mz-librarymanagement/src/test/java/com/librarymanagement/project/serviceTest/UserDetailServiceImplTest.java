package com.librarymanagement.project.serviceTest;


import com.librarymanagement.project.MzLibrarymanagementApplication;
import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.models.AppRole;
import com.librarymanagement.project.models.Role;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = MzLibrarymanagementApplication.class)
public class UserDetailServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void TestLoadUsernameSuccess(){
        // set up user
        String email = "test@user.com";
        String userName = "Test user";
        User user = new User();
        user.setUserId(1L);
        user.setUserName(userName);
        user.setEmail(email);
        user.setRoles(new HashSet<>(Set.of(new Role(10, AppRole.ROLE_USER))));
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user)); // mock repository behavior

        // Execute the method
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        // Check returned user details' fields
        assertNotNull(userDetails);
        assertEquals(userDetails.getUsername(), userName);
        assertEquals(userDetails.getAuthorities().size(), 1);
        assert userDetails.getAuthorities().contains(new SimpleGrantedAuthority(AppRole.ROLE_USER.name()));

    }

    @Test
    public void TestLoadUsernameFailUsernameNotFound(){
        // set up user
        String userName = "Test user";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty()); // mock repository behavior

        // Execute the method
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(userName));

        assertEquals("User not found by user name: " + userName, exception.getMessage());
    }

}
