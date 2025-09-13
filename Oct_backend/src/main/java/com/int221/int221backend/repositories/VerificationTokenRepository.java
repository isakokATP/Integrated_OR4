package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Users;
import com.int221.int221backend.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    VerificationToken findByToken(String token);

    void deleteByUser(Users user);
}
