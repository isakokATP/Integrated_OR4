package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.order.OrderItemDto;
import com.int221.int221backend.dto.request.order.PlaceOrderRequestDto;
import com.int221.int221backend.dto.request.order.PlaceOrderSellerGroupDto;
import com.int221.int221backend.dto.response.history.OrderSummaryDto;
import com.int221.int221backend.dto.response.order.OrderResponseDto;
import com.int221.int221backend.entities.*;
import com.int221.int221backend.enums.OrderStatus;
import com.int221.int221backend.exception.InsufficientStockException;
import com.int221.int221backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
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

//    @Transactional
//    public OrderResponseDto placeOrder(Long buyerUserId, PlaceOrderRequestDto requestDto) {
//
//        Users buyer = userRepository.findById(buyerUserId.intValue())
//                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + buyerUserId));
//
//        Order newOrder = new Order();
//        newOrder.setBuyer(buyer);
//        newOrder.setOrderTimestamp(Instant.now());
//        newOrder.setStatus(OrderStatus.PENDING);
//
//        newOrder.setShippingAddress(requestDto.getShippingAddress());
//        newOrder.setNote(requestDto.getNote());
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        BigDecimal calculatedTotalPrice = BigDecimal.ZERO;
//        int calculatedTotalItems = 0;
//
//        for (PlaceOrderSellerGroupDto sellerGroup : requestDto.getSellerGroups()) {
//            for (OrderItemDto itemDto : sellerGroup.getItems()) {
//                SaleItem saleItem = saleItemRepository.findById(itemDto.getSaleItemId())
//                        // .withLock(LockModeType.PESSIMISTIC_WRITE)
//                        .orElseThrow(() -> new ResourceNotFoundException("Sale item not found: " + itemDto.getSaleItemId()));
//
//                int requestedQuantity = itemDto.getQuantity();
//                long currentStock = saleItem.getQuantity();
//
//                if (currentStock < requestedQuantity) {
//                    throw new InsufficientStockException("Insufficient stock for item: "
//                            + saleItem.getModel() + " (ID: " + saleItem.getId() + "). Available: "
//                            + currentStock + ", Requested: " + requestedQuantity);
//                }
//
//                saleItem.setQuantity(currentStock - requestedQuantity);
//                OrderItem orderItem = new OrderItem();
//                // orderItem.setOrder(newOrder); // Link will be set via cascade or helper method
//                orderItem.setSaleItem(saleItem);
//                orderItem.setQuantity(requestedQuantity);
//                orderItem.setPriceAtOrder(BigDecimal.valueOf(saleItem.getPrice())); // Record price at time of order
//                // Use helper method to manage bidirectional relationship
//                newOrder.addItem(orderItem);
//
//                cartItemRepository.findByUserAndSaleItem(buyer, saleItem)
//                        .ifPresent(cartItemRepository::delete); // Delete if found
//
//                // Calculate running totals
//                calculatedTotalPrice = calculatedTotalPrice.add(
//                        orderItem.getPriceAtOrder().multiply(BigDecimal.valueOf(requestedQuantity))
//                );
//                calculatedTotalItems += requestedQuantity;
//            }
//        }
//
//        newOrder.setTotalPrice(calculatedTotalPrice);
//        newOrder.setTotalItems(calculatedTotalItems);
//
//        Order savedOrder = orderRepository.save(newOrder);
//
//        return OrderResponseDto.builder()
//                .orderId(savedOrder.getId())
//                .orderTimestamp(savedOrder.getOrderTimestamp())
//                .totalItems(savedOrder.getTotalItems())
//                .totalPrice(savedOrder.getTotalPrice())
//                .shippingAddress(savedOrder.getShippingAddress())
//                .note(savedOrder.getNote())
//                .build();
//    }

    @Transactional
    public List<OrderResponseDto> placeOrder(Long buyerUserId, PlaceOrderRequestDto requestDto) {
        Users buyer = userRepository.findById(buyerUserId.intValue()) // หรือ .longValue() ถ้า repository รับ Long
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with id: " + buyerUserId));
        List<OrderResponseDto> resultList = new ArrayList<>();

        for (PlaceOrderSellerGroupDto sellerGroup : requestDto.getSellerGroups()) {
            Users seller = userRepository.findById(sellerGroup.getSellerId().intValue()) // แปลงเป็น int หรือ long ตาม Repository
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerGroup.getSellerId()));

            boolean isStockSufficient = true;

            for (OrderItemDto itemDto : sellerGroup.getItems()) {
                SaleItem itemToCheck = saleItemRepository.findById(itemDto.getSaleItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Sale item not found: " + itemDto.getSaleItemId()));

                boolean existsInCart = cartItemRepository.findByUserAndSaleItem(buyer, itemToCheck).isPresent();
                if (!existsInCart) {
                    throw new IllegalArgumentException("Item not found in cart or already ordered: " + itemToCheck.getModel());
                }

                if (itemToCheck.getQuantity() < itemDto.getQuantity()) {
                    isStockSufficient = false;
                }
            }

            Order newOrder = new Order();
            newOrder.setBuyer(buyer);
            newOrder.setSeller(seller);
            newOrder.setOrderTimestamp(Instant.now());
            newOrder.setShippingAddress(requestDto.getShippingAddress());
            newOrder.setNote(requestDto.getNote());
            newOrder.setIsViewed(false);

            if (isStockSufficient) {
                newOrder.setStatus(OrderStatus.COMPLETED);
            } else {
                newOrder.setStatus(OrderStatus.CANCELLED);
            }

            BigDecimal groupTotalPrice = BigDecimal.ZERO;
            int groupTotalItems = 0;

            for (OrderItemDto itemDto : sellerGroup.getItems()) {
                SaleItem saleItem = saleItemRepository.findById(itemDto.getSaleItemId()).get();
                int requestedQuantity = itemDto.getQuantity();

                if (isStockSufficient) {
                    saleItem.setQuantity(saleItem.getQuantity() - requestedQuantity);
                    saleItemRepository.save(saleItem);
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setSaleItem(saleItem);
                orderItem.setQuantity(requestedQuantity);
                orderItem.setPriceAtOrder(BigDecimal.valueOf(saleItem.getPrice()));

                newOrder.addItem(orderItem);

                CartItem cartItem = cartItemRepository.findByUserAndSaleItem(buyer, saleItem)
                        .orElseThrow(() -> new IllegalArgumentException("Cart item missing"));
                cartItemRepository.delete(cartItem);

                groupTotalPrice = groupTotalPrice.add(orderItem.getPriceAtOrder().multiply(BigDecimal.valueOf(requestedQuantity)));
                groupTotalItems += requestedQuantity;
            }

            newOrder.setTotalPrice(groupTotalPrice);
            newOrder.setTotalItems(groupTotalItems);

            Order savedOrder = orderRepository.save(newOrder);

            resultList.add(toOrderResponseDto(savedOrder));

        }
        return resultList;
    }

    public List<OrderResponseDto> getSellerOrders(Long sellerId, String type) {
        List<Order> orders;

        // เลือก Query ตามประเภท Tab
        switch (type.toLowerCase()) {
            case "new":
                orders = orderRepository.findBySellerIdAndStatusAndIsViewedFalseOrderByOrderTimestampDesc(
                        sellerId.intValue(), OrderStatus.COMPLETED);
                break;
            case "canceled":
                orders = orderRepository.findBySellerIdAndStatusOrderByOrderTimestampDesc(
                        sellerId.intValue(), OrderStatus.CANCELLED);
                break;
            case "all": // Completed & Viewed
                orders = orderRepository.findBySellerIdAndStatusAndIsViewedTrueOrderByOrderTimestampDesc(
                        sellerId.intValue(), OrderStatus.COMPLETED);
                break;
            default:
                throw new IllegalArgumentException("Invalid order type: " + type);
        }

        return orders.stream().map(this::toOrderResponseDto).collect(Collectors.toList());
    }

    private OrderResponseDto toOrderResponseDto(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .orderTimestamp(order.getOrderTimestamp())
                .totalItems(order.getTotalItems())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .orderStatus(OrderStatus.valueOf(order.getStatus().name()))

                .build();
    }

    public List<OrderSummaryDto> getOrderHistory(Long buyerUserId) {
        List<Order> orders = orderRepository.findByBuyerIdWithDetailsOrderByOrderTimestampDesc(buyerUserId.intValue());

        return orders.stream()
                .map(OrderSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markOrderAsViewed(Long orderId, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Authorization Check: ต้องเป็น Seller เจ้าของออเดอร์เท่านั้น
        if (!order.getSeller().getId().equals(sellerId.intValue())) {
            throw new AccessDeniedException("You do not have permission to modify this order.");
        }

        order.setIsViewed(true);
        orderRepository.save(order);
    }
}
