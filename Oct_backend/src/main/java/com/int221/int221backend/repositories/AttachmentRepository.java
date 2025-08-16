package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
