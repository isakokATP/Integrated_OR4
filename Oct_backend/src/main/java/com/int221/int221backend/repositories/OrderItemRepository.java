package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Order;
import com.int221.int221backend.entities.OrderItem;
import com.int221.int221backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
