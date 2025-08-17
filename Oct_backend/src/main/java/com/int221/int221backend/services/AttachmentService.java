package com.int221.int221backend.services;

import com.int221.int221backend.dto.response.AttachmentDto;
import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.enums.FileType;
import com.int221.int221backend.repositories.AttachmentRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir; // รับค่าจาก application.properties

    private final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    public AttachmentDto saveAttachment(Integer saleItemId, MultipartFile file) throws IOException {
        // ตรวจสอบ SaleItem
        SaleItem saleItem = saleItemRepository.findById(saleItemId)
                .orElseThrow(() -> new RuntimeException("SaleItem not found with id " + saleItemId));
        if (attachmentRepository.countAttachmentBySaleItem(saleItem) >= 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload more than 4 attachments");
        }

        // ตรวจสอบนามสกุลไฟล์
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException("Invalid file name");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

        // แปลงนามสกุลเป็น enum FileType แบบ ignore case
        String extLower = extension.toLowerCase();
        FileType fileType;
        switch (extLower) {
            case "jpg": fileType = FileType.JPEG; break;
            case "png": fileType = FileType.PNG; break;
            default: throw new IllegalArgumentException("Invalid file type");
        }

        // ตรวจสอบขนาดไฟล์
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 2MB");
        }

        // สร้างโฟลเดอร์ถ้ายังไม่มี
        Files.createDirectories(Paths.get(uploadDir));

        // สร้าง path สำหรับไฟล์
        String newFilename = UUID.randomUUID() + "." + extLower;
        Path filePath = Paths.get(uploadDir, newFilename);
        Files.write(filePath, file.getBytes());

        // หาลำดับสูงสุดของรูปที่มีอยู่แล้วใน saleItem
        Integer maxOrder = attachmentRepository.findMaxImageViewOrderBySaleItem(saleItem);

        // ถ้ายังไม่มีรูปเลย เริ่มที่ 1, ถ้ามีแล้ว +1
        int newOrder = (maxOrder == null ? 1 : maxOrder + 1);

        // สร้าง Attachment และเซ็ตข้อมูล
        Attachment attachment = new Attachment();
        attachment.setSaleItem(saleItem);
        attachment.setFilename(newFilename);
        attachment.setFilePath(filePath.toString());
        attachment.setFileSize((int) file.getSize());
        attachment.setFileType(fileType);
        attachment.setImageViewOrder(newOrder);


        // บันทึกลง DB
//        return attachmentRepository.save(attachment);
        return modelMapper.map(attachmentRepository.save(attachment), AttachmentDto.class);
    }

    public Attachment getAttachmentById(Integer id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id " + id));
    }
}
