package com.int221.int221backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "verification_tokens", schema = "pbi1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // ผูกกับ user คนเดียว (OneToOne)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
