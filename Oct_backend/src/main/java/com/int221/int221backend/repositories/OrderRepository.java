package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Order;
import com.int221.int221backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items oi JOIN FETCH oi.saleItem si JOIN FETCH si.brand JOIN FETCH si.seller WHERE o.buyer.id = :buyerId ORDER BY o.orderTimestamp DESC")
    List<Order> findByBuyerIdWithDetailsOrderByOrderTimestampDesc(Integer buyerId);

    // 1. สำหรับ Tab "New Orders" (Completed + Not Viewed)
    List<Order> findBySellerIdAndStatusAndIsViewedFalseOrderByOrderTimestampDesc(Integer sellerId, OrderStatus status);

    // 2. สำหรับ Tab "Canceled Orders" (Cancelled)
    List<Order> findBySellerIdAndStatusOrderByOrderTimestampDesc(Integer sellerId, OrderStatus status);

    // 3. สำหรับ Tab "All Orders" (Completed + Viewed)
    List<Order> findBySellerIdAndStatusAndIsViewedTrueOrderByOrderTimestampDesc(Integer sellerId, OrderStatus status);
}
