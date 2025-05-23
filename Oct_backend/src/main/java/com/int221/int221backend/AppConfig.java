package com.int221.int221backend;

import com.int221.int221backend.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public ListMapper listMapper() {
        return new ListMapper();
    }
}
