package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.AttachmentUploadDto;
import com.int221.int221backend.dto.response.AttachmentDto;
import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.services.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/attachments")
@CrossOrigin(origins = "*")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<AttachmentDto> uploadFile(@ModelAttribute AttachmentUploadDto dto) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.saveAttachment(dto.getSaleItemId(), dto.getFile()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAttachment(@PathVariable Integer id) throws IOException {
        Attachment attachment = attachmentService.getAttachmentById(id);
        Path path = Paths.get(attachment.getFilePath());
        byte[] fileData = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/" + attachment.getFileType().name().toLowerCase()));
        headers.setContentLength(fileData.length);

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}
