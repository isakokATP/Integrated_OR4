package com.int221.int221backend.services;

import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(Long id) {
        Users user = userRepository.findById(id.intValue())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(Users user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getUserType() != null) {
            authorities.add(new SimpleGrantedAuthority(user.getUserType().name()));
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}