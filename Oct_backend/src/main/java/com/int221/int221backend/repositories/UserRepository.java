package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByIdCardNumber(String idCardNumber);
}
