package com.int221.int221backend.services;

import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean matchPassword(String email, String password) {
        Optional<Users> usersOptional = userRepository.findByEmail(email);
        if( usersOptional.isEmpty()) {
            return false;
        }
        Users users = usersOptional.get();
        boolean passwordMatches = passwordEncoder.matches(password, users.getPassword());
        return passwordMatches;
    }
}
