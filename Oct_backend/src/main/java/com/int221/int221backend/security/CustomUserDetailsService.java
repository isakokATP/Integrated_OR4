package com.int221.int221backend.security;

import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ค้นหาผู้ใช้จาก email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ตรวจสอบว่า user status เป็น ACTIVE หรือไม่
        if (user.getStatus() != Users.Status.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active: " + email);
        }

        // สร้าง UserDetails object
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // password ถูก encode แล้วใน DB
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
