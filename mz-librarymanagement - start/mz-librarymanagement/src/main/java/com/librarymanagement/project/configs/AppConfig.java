package com.librarymanagement.project.configs;

import com.librarymanagement.project.models.User;
import com.librarymanagement.project.payloads.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Configuration class for setting up global beans and configurations.
 */
@Configuration
public class AppConfig {

    /**
     * Bean configuration for ModelMapper.
     * This configuration customizes the mapping for the User entity to UserDTO,
     * skipping the password field during mapping.
     *
     * @return a configured ModelMapper instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Ignore mapping password
        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> mapper.skip(UserDTO::setPassword));

        return modelMapper;
    }

}
