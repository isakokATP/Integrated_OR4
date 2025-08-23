package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Integer countAttachmentBySaleItem(SaleItem saleItem);

    @Query("SELECT MAX(a.imageViewOrder) FROM Attachment a WHERE a.saleItem = :saleItem")
    Integer findMaxImageViewOrderBySaleItem(@Param("saleItem") SaleItem saleItem);

    List<Attachment> findBySaleItemId(Integer saleItemId);
}
