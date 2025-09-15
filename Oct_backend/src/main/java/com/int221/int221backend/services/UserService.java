package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.UserRequestDto;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.entities.VerificationToken;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
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



    // Register User
    @Transactional
    public UserResponseDto registerUser(UserRequestDto requestDto,
                                        MultipartFile idCardImageFront,
                                        MultipartFile idCardImageBack) {

        // 1. เช็ค email ซ้ำ
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + requestDto.getEmail());
        }

        // 2. เช็ค ID card number ซ้ำ
        if (userRepository.existsByIdCardNumber(requestDto.getIdCardNumber())) {
            throw new RuntimeException("ID card number already exists: " + requestDto.getIdCardNumber());
        }

        try {
            // 3. บันทึกไฟล์รูป
            String frontFilePath = saveFile(idCardImageFront);
            String backFilePath = saveFile(idCardImageBack);

            // 4. สร้าง entity user
            Users user = Users.builder()
                    .nickName(requestDto.getNickName())
                    .email(requestDto.getEmail())
                    .fullName(requestDto.getFullName())
                    .password(passwordEncoder.encode(requestDto.getPassword())) // เข้ารหัส
                    .phoneNumber(requestDto.getPhoneNumber())
                    .bankAccount(requestDto.getBankAccount())
                    .idCardNumber(requestDto.getIdCardNumber())
                    .userType(Users.UserType.valueOf(requestDto.getUserType().toUpperCase()))
                    .idCardImageFront(frontFilePath)
                    .idCardImageBack(backFilePath)
                    .status(Users.Status.INACTIVE) // default = INACTIVE
                    .build();

            // 5. บันทึก user ลง DB
            Users savedUser = userRepository.saveAndFlush(user);
            entityManager.refresh(savedUser);

            // 6. สร้าง Verification Token
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = VerificationToken.builder()
                    .token(token)
                    .expiryDate(LocalDateTime.now().plusHours(24))
                    .user(savedUser)
                    .build();
            tokenRepository.save(verificationToken);

            // 7. ส่ง Email Verification (จับ exception ภายใน)
            try {
                emailService.sendVerificationEmail(savedUser, token);
            } catch (Exception e) {
                // log หรือ throw runtime ถ้าต้องการ
                throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
            }

            // 8. map → DTO และ return
            return UserResponseDto.fromEntity(savedUser);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage(), e);
        }
    }

    // helper method สำหรับบันทึกไฟล์ และคืน path เต็ม
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // สร้าง folder ถ้ายังไม่มี
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        // บันทึกไฟล์จริง
        file.transferTo(filePath.toFile());

        // คืนค่าเป็น path เต็ม (absolute path)
        return filePath.toAbsolutePath().toString();
    }


    // ดึง User ตาม ID
    public UserResponseDto getUserById(Integer id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponseDto.fromEntity(user);
    }

    // Mapping Entity -> ResponseDto
    private UserResponseDto mapToDto(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .bankAccount(user.getBankAccount())
                .idCardNumber(user.getIdCardNumber())
                .userType(user.getUserType().name())
                .idCardImageFront(user.getIdCardImageFront())
                .idCardImageBack(user.getIdCardImageBack())
                .build();
    }
}
