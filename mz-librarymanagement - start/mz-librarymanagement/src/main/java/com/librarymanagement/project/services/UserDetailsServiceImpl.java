package com.librarymanagement.project.services;

import com.librarymanagement.project.exceptions.ResourceNotFoundException;
import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service implementation for loading user details based on the username.
 * This service is used by Spring Security to fetch the user details during authentication.
 * It retrieves the user information from the database and converts it into a UserDetails object.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Loads the user details by the provided username.
     * This method is used by Spring Security to authenticate the user and load the necessary details.
     *
     * @param username The username of the user whose details need to be loaded.
     * @return The UserDetails object containing the user information, including roles and authorities.
     * @throws UsernameNotFoundException If the user is not found in the database.
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow( () ->
                        new ResourceNotFoundException("User not found by user name: " + username));


        return UserDetailsImpl.build(user);
    }
}
