package com.int221.int221backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "users", schema = "pbi1")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nick_name", nullable = false, length = 100)
    private String nickName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "id_card_number", length = 20)
    private String idCardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 10)
    private UserType userType;

    @Column(name = "id_card_image_front")
    private String idCardImageFront;

    @Column(name = "id_card_image_back")
    private String idCardImageBack;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    public enum UserType {
        SELLER,
        BUYER
    }
    public enum Status {
        INACTIVE,
        ACTIVE
    }
}
