package com.librarymanagement.project.services;

import com.librarymanagement.project.models.User;
import com.librarymanagement.project.repositories.UserRepository;
import com.librarymanagement.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow( () ->
                        new UsernameNotFoundException("User not found by user name: " + username));


        return UserDetailsImpl.build(user);
    }
}
