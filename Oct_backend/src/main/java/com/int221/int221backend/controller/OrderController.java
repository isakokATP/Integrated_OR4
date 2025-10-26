package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.order.PlaceOrderRequestDto;
import com.int221.int221backend.dto.response.ErrorResponse;
import com.int221.int221backend.dto.response.history.OrderSummaryDto;
import com.int221.int221backend.dto.response.order.OrderResponseDto;
import com.int221.int221backend.exception.InsufficientStockException;
import com.int221.int221backend.security.JwtTokenProvider;
import com.int221.int221backend.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/v2/orders")
    public ResponseEntity<?> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long buyerUserId = getUserIdFromRequest(request);

            OrderResponseDto orderResponse = orderService.placeOrder(buyerUserId, requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        } catch (InsufficientStockException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to place order: " + e.getMessage()));
        }
    }

    @GetMapping("/v2/users/{id}/orders")
    public ResponseEntity<?> getOrderHistory(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long loggedInUserId = getUserIdFromRequest(request);

            if (!loggedInUserId.equals(id)) {
                throw new AccessDeniedException("Access Denied: You can only view your own orders.");
            }

            List<OrderSummaryDto> orderHistory = orderService.getOrderHistory(id);

            return ResponseEntity.ok(orderHistory);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred fetching order history: " + e.getMessage()));
        }
    }

    private Long getUserIdFromRequest(HttpServletRequest request) throws SecurityException {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new SecurityException("Invalid or missing token");
        }
        Long userId = jwtTokenProvider.extractId(token);
        if (userId == null) {
            throw new SecurityException("Token missing user ID");
        }
        return userId;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
