package com.int221.int221backend.dto.response.order;

import com.int221.int221backend.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Instant orderTimestamp;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private OrderStatus orderStatus;
    private String note;
    private java.util.List<com.int221.int221backend.dto.response.history.OrderItemDetailDto> items;
    private String buyerNickname;
}
