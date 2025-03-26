package com.librarymanagement.project;

import com.librarymanagement.project.security.DataInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public DataInitializer dataInitializer(){
        return new DataInitializer(){
            @Override
            public void init(){}
        };
    }
}
