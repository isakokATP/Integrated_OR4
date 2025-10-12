package com.int221.int221backend.services;

import com.int221.int221backend.entities.Users;
import com.int221.int221backend.enums.AuthStatus;
import com.int221.int221backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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

    public Users authenticate(String email, String password) {
        // 1. ค้นหาผู้ใช้ด้วยอีเมล ถ้าไม่เจอ โยน Exception ทันที
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email/password"));

        // 2. ตรวจสอบรหัสผ่าน ถ้าไม่ตรง โยน Exception เดียวกัน
        // (เราใช้ Exception เดียวกันเพื่อป้องกันการเดาว่าอีเมลไหนมีในระบบ)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email/password");
        }

        // 3. ตรวจสอบสถานะบัญชี ถ้าไม่ใช่ ACTIVE โยน Exception
        if (user.getStatus() != Users.Status.ACTIVE) {
            throw new DisabledException("You need to activate your account before signing in.");
        }

        // 4. ถ้าทุกอย่างผ่านหมด คืนค่า object Users กลับไป
        return user;
    }

}
