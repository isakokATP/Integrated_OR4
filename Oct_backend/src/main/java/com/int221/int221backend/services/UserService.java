package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.userrequest.UpdateProfileRequestDto;
import com.int221.int221backend.dto.request.userrequest.UserRequestDto;
import com.int221.int221backend.dto.response.userresponse.BuyerProfileResponseDto;
import com.int221.int221backend.dto.response.userresponse.SellerProfileResponseDto;
import com.int221.int221backend.dto.response.userresponse.UserProfileResponseDto;
import com.int221.int221backend.dto.response.userresponse.UserResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.exception.DuplicateResourceException;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

//    public UserProfileResponseDto getUserProfileById(Integer userId) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
//        return UserProfileResponseDto.fromEntity(user);
//    }

    @Transactional
    public UserProfileResponseDto updateUserProfile(Integer userId, UpdateProfileRequestDto updateDto) {
        Users userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // อัปเดตเฉพาะ field ที่อนุญาตให้แก้ไข
        userToUpdate.setNickName(updateDto.getNickname());
        userToUpdate.setFullName(updateDto.getFullname());

        Users updatedUser = userRepository.save(userToUpdate);
        return UserProfileResponseDto.fromEntity(updatedUser);
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

    public Object getUserProfileById(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        switch (user.getUserType()) {
            case BUYER:
                return BuyerProfileResponseDto.fromEntity(user);
            case SELLER:
                return SellerProfileResponseDto.fromEntity(user);
            default:
                // กรณีมี Role อื่นๆ ที่ไม่ได้คาดไว้ อาจจะคืนค่าพื้นฐานหรือโยน Exception
                throw new IllegalStateException("Unexpected user type: " + user.getUserType());
        }
    }
}
