package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Integer countAttachmentBySaleItem(SaleItem saleItem);
}
