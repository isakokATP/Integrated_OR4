package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.UserRequestDto;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register User
    public UserResponseDto registerUser(UserRequestDto requestDto,
                                        MultipartFile idCardImageFront,
                                        MultipartFile idCardImageBack) {

        try {
            // เช็ค email ซ้ำ
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + requestDto.getEmail());
            }

            if (userRepository.existsByIdCardNumber(requestDto.getIdCardNumber())) {
                throw new RuntimeException("ID card number already exists: " + requestDto.getIdCardNumber());
            }

            // บันทึกไฟล์ลงโฟลเดอร์
            String frontFileName = saveFile(idCardImageFront);
            String backFileName = saveFile(idCardImageBack);

            // แปลงจาก DTO → Entity
            Users user = new Users();
            user.setNickName(requestDto.getNickName());
            user.setEmail(requestDto.getEmail());
            user.setFullName(requestDto.getFullName());
            user.setPassword(requestDto.getPassword()); // ยังไม่เข้ารหัส
            user.setPhoneNumber(requestDto.getPhoneNumber());
            user.setBankAccount(requestDto.getBankAccount());
            user.setIdCardNumber(requestDto.getIdCardNumber());
            user.setUserType(Users.UserType.valueOf(requestDto.getUserType()));
            user.setIdCardImageFront(frontFileName);
            user.setIdCardImageBack(backFileName);

            // บันทึกลงฐานข้อมูล
            Users savedUser = userRepository.save(user);

            // แปลง Entity → DTO สำหรับ response
            return UserResponseDto.fromEntity(savedUser);

        } catch (IOException e) {
            throw new RuntimeException("Upload image failed: " + e.getMessage());
        }
    }

    // helper method สำหรับบันทึกไฟล์
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String uploadDir = "uploads/idcards/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        file.transferTo(path);
        return filename;
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
