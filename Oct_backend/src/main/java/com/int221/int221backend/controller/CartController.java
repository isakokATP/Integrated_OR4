package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.cart.AddToCartRequestDto;
import com.int221.int221backend.dto.request.cart.UpdateCartItemQuantityDto;
import com.int221.int221backend.dto.response.ErrorResponse;
import com.int221.int221backend.dto.response.cart.CartItemResponseDto;
import com.int221.int221backend.dto.response.cart.ViewCartResponseDto;
import com.int221.int221backend.exception.InsufficientStockException;
import com.int221.int221backend.security.JwtTokenProvider;
import com.int221.int221backend.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/v2/cart/items")
    public ResponseEntity<?> addItemToCart(@Valid @RequestBody AddToCartRequestDto requestDto,
                                           HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid or missing token"));
            }
            Long userId = jwtTokenProvider.extractId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token missing user ID"));
            }

            CartItemResponseDto cartItem = cartService.addItemToCart(userId, requestDto);

            return ResponseEntity.ok(cartItem);

        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/v2/cart")
    public ResponseEntity<?> viewCart(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            ViewCartResponseDto cartContents = cartService.viewCart(userId);
            return ResponseEntity.ok(cartContents);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/v2/cart/items/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityDto updateDto,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            CartItemResponseDto updatedItem = cartService.updateCartItemQuantity(userId, cartItemId, updateDto);
            return ResponseEntity.ok(updatedItem);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));

        } catch (InsufficientStockException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/v2/cart/items/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Integer cartItemId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);

            cartService.removeCartItem(userId, cartItemId);

            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
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
