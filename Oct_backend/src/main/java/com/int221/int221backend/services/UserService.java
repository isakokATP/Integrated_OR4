package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.UserRequestDto;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.entities.VerificationToken;
import com.int221.int221backend.exception.DuplicateResourceException;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir.users}")
    private String uploadDir;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponseDto registerUser(UserRequestDto requestDto,
                                        MultipartFile idCardImageFront,
                                        MultipartFile idCardImageBack) {
        if ("SELLER".equals(requestDto.getUserType())) {
            if (requestDto.getPhoneNumber() == null || requestDto.getPhoneNumber().trim().isEmpty()) {
                throw new RuntimeException("Phone number is required for SELLER");
            }
            if (requestDto.getBankAccount() == null || requestDto.getBankAccount().trim().isEmpty()) {
                throw new RuntimeException("Bank account is required for SELLER");
            }
            if (requestDto.getIdCardNumber() == null || requestDto.getIdCardNumber().trim().isEmpty()) {
                throw new RuntimeException("National ID number is required for SELLER");
            }
            if (idCardImageFront == null || idCardImageFront.isEmpty()) {
                throw new RuntimeException("ID card front image is required for SELLER");
            }
            if (idCardImageBack == null || idCardImageBack.isEmpty()) {
                throw new RuntimeException("ID card back image is required for SELLER");
            }
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDto.getEmail());
        }
        
        String idCardNumber = requestDto.getIdCardNumber();
        if (idCardNumber != null && !idCardNumber.trim().isEmpty()) {
            if (userRepository.existsByIdCardNumber(idCardNumber.trim())) {
                throw new RuntimeException("ID card number already exists: " + idCardNumber);
            }
        }

        try {
            String frontFilePath = saveFile(idCardImageFront);
            String backFilePath = saveFile(idCardImageBack);

            Users user = Users.builder()
                    .nickName(requestDto.getNickName())
                    .email(requestDto.getEmail())
                    .fullName(requestDto.getFullName())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .phoneNumber(requestDto.getPhoneNumber())
                    .bankName(requestDto.getBankName())
                    .bankAccount(requestDto.getBankAccount())
                    .idCardNumber(requestDto.getIdCardNumber())
                    .userType(Users.UserType.valueOf(requestDto.getUserType().toUpperCase()))
                    .idCardImageFront(frontFilePath)
                    .idCardImageBack(backFilePath)
                    .status(Users.Status.INACTIVE)
                    .build();

            Users savedUser = userRepository.saveAndFlush(user);
            entityManager.refresh(savedUser);

            String jwtToken = jwtTokenProvider.generateToken(
                    savedUser.getId().longValue(),
                    savedUser.getEmail()
            );

            try {
                emailService.sendVerificationEmail(savedUser, jwtToken);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
            }

            return UserResponseDto.fromEntity(savedUser);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage(), e);
        }
    }

    // helper method สำหรับบันทึกไฟล์ และคืน path เต็ม
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        file.transferTo(filePath.toFile());

        return filePath.toAbsolutePath().toString();
    }


    // ดึง User ตาม ID
    public UserResponseDto getUserById(Integer id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponseDto.fromEntity(user);
    }

    private UserResponseDto mapToDto(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .bankName(user.getBankName())
                .bankAccount(user.getBankAccount())
                .idCardNumber(user.getIdCardNumber())
                .userType(user.getUserType().name())
                .idCardImageFront(user.getIdCardImageFront())
                .idCardImageBack(user.getIdCardImageBack())
                .build();
    }
}
