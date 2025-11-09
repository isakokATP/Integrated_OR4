package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.order.OrderItemDto;
import com.int221.int221backend.dto.request.order.PlaceOrderRequestDto;
import com.int221.int221backend.dto.request.order.PlaceOrderSellerGroupDto;
import com.int221.int221backend.dto.response.history.OrderSummaryDto;
import com.int221.int221backend.dto.response.order.OrderResponseDto;
import com.int221.int221backend.entities.Order;
import com.int221.int221backend.entities.OrderItem;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.enums.OrderStatus;
import com.int221.int221backend.exception.InsufficientStockException;
import com.int221.int221backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponseDto placeOrder(Long buyerUserId, PlaceOrderRequestDto requestDto) {

        Users buyer = userRepository.findById(buyerUserId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + buyerUserId));

        Order newOrder = new Order();
        newOrder.setBuyer(buyer);
        newOrder.setOrderTimestamp(Instant.now());
        newOrder.setStatus(OrderStatus.PENDING);

        newOrder.setShippingAddress(requestDto.getShippingAddress());
        newOrder.setNote(requestDto.getNote());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal calculatedTotalPrice = BigDecimal.ZERO;
        int calculatedTotalItems = 0;

        for (PlaceOrderSellerGroupDto sellerGroup : requestDto.getSellerGroups()) {
            for (OrderItemDto itemDto : sellerGroup.getItems()) {
                SaleItem saleItem = saleItemRepository.findById(itemDto.getSaleItemId())
                        // .withLock(LockModeType.PESSIMISTIC_WRITE)
                        .orElseThrow(() -> new ResourceNotFoundException("Sale item not found: " + itemDto.getSaleItemId()));

                int requestedQuantity = itemDto.getQuantity();
                long currentStock = saleItem.getQuantity();

                if (currentStock < requestedQuantity) {
                    throw new InsufficientStockException("Insufficient stock for item: "
                            + saleItem.getModel() + " (ID: " + saleItem.getId() + "). Available: "
                            + currentStock + ", Requested: " + requestedQuantity);
                }

                saleItem.setQuantity(currentStock - requestedQuantity);
                OrderItem orderItem = new OrderItem();
                // orderItem.setOrder(newOrder); // Link will be set via cascade or helper method
                orderItem.setSaleItem(saleItem);
                orderItem.setQuantity(requestedQuantity);
                orderItem.setPriceAtOrder(BigDecimal.valueOf(saleItem.getPrice())); // Record price at time of order
                // Use helper method to manage bidirectional relationship
                newOrder.addItem(orderItem);

                cartItemRepository.findByUserAndSaleItem(buyer, saleItem)
                        .ifPresent(cartItemRepository::delete); // Delete if found

                // Calculate running totals
                calculatedTotalPrice = calculatedTotalPrice.add(
                        orderItem.getPriceAtOrder().multiply(BigDecimal.valueOf(requestedQuantity))
                );
                calculatedTotalItems += requestedQuantity;
            }
        }

        newOrder.setTotalPrice(calculatedTotalPrice);
        newOrder.setTotalItems(calculatedTotalItems);

        Order savedOrder = orderRepository.save(newOrder);

        return OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .orderTimestamp(savedOrder.getOrderTimestamp())
                .totalItems(savedOrder.getTotalItems())
                .totalPrice(savedOrder.getTotalPrice())
                .shippingAddress(savedOrder.getShippingAddress())
                .note(savedOrder.getNote())
                .build();
    }

    public List<OrderSummaryDto> getOrderHistory(Long buyerUserId) {
        List<Order> orders = orderRepository.findByBuyerIdWithDetailsOrderByOrderTimestampDesc(buyerUserId.intValue());

        return orders.stream()
                .map(OrderSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }
}
