package com.int221.int221backend.dto.response.history;

import com.int221.int221backend.entities.Order;
import com.int221.int221backend.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderSummaryDto {
    private Long orderNumber;
    private Instant orderDate;
    // private Instant paymentDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String note;
    private List<OrderItemDetailDto> items;

    public static OrderSummaryDto fromEntity(Order order) {
        List<OrderItemDetailDto> itemDtos = order.getItems().stream()
                .map(OrderItemDetailDto::fromEntity)
                .collect(Collectors.toList());

        return OrderSummaryDto.builder()
                .orderNumber(order.getId())
                .orderDate(order.getOrderTimestamp())
                // .paymentDate(order.getPaymentTimestamp()) // ⬅️ เพิ่ม ถ้ามี
                .totalAmount(order.getTotalPrice())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .items(itemDtos)
                .build();
    }
}
