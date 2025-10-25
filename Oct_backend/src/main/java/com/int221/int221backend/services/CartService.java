package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.cart.AddToCartRequestDto;
import com.int221.int221backend.dto.request.cart.UpdateCartItemQuantityDto;
import com.int221.int221backend.dto.response.cart.CartItemDetailDto;
import com.int221.int221backend.dto.response.cart.CartItemResponseDto;
import com.int221.int221backend.dto.response.cart.CartSellerGroupDto;
import com.int221.int221backend.dto.response.cart.ViewCartResponseDto;
import com.int221.int221backend.entities.CartItem;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.exception.InsufficientStockException;
import com.int221.int221backend.repositories.CartItemRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    public ViewCartResponseDto viewCart(Long userId) {
        List<CartItem> userCartItems = cartItemRepository.findByUserIdOrderBySellerIdAsc(userId.intValue());

        Map<Users, List<CartItemDetailDto>> groupedBySeller = userCartItems.stream()
                .collect(Collectors.groupingBy(
                        CartItem::getSeller, // Group by the seller entity
                        LinkedHashMap::new, // Maintain insertion order (optional)
                        Collectors.mapping(CartItemDetailDto::fromEntity, Collectors.toList()) // Convert items to DTOs
                ));

        List<CartSellerGroupDto> sellerGroups = groupedBySeller.entrySet().stream()
                .map(entry -> new CartSellerGroupDto(
                        entry.getKey().getId(),
                        entry.getKey().getNickName(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
        int totalItems = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : userCartItems) {
            totalItems += item.getQuantity();
            BigDecimal itemPrice = BigDecimal.valueOf(item.getSaleItem().getPrice()); // แปลง Integer เป็น BigDecimal
            totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()))); // ราคา * จำนวน
        }

        return new ViewCartResponseDto(sellerGroups, totalItems, totalPrice);
    }

    @Transactional
    public CartItemResponseDto addItemToCart(Long userId, AddToCartRequestDto requestDto) {
        Users buyer = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        SaleItem saleItem = saleItemRepository.findById(requestDto.getSaleItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found with id: " + requestDto.getSaleItemId()));

        Users seller = saleItem.getSeller();
        if (seller == null) {
            throw new IllegalStateException("Sale item " + saleItem.getId() + " does not have an associated seller.");
        }

        long availableStock = saleItem.getQuantity();
        int requestedQuantity = requestDto.getQuantity();

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndSaleItem(buyer, saleItem);

        CartItem cartItemToSave;

        if (existingCartItemOpt.isPresent()) {
            cartItemToSave = existingCartItemOpt.get();
            int newQuantity = cartItemToSave.getQuantity() + requestedQuantity;

            if (newQuantity > availableStock) {
                throw new InsufficientStockException("Cannot add quantity. Available stock: " + availableStock + ", Requested total: " + newQuantity);
            }
            cartItemToSave.setQuantity(newQuantity);

        } else {
            if (requestedQuantity > availableStock) {
                throw new InsufficientStockException("Cannot add item. Available stock: " + availableStock + ", Requested: " + requestedQuantity);
            }

            cartItemToSave = new CartItem();
            cartItemToSave.setUser(buyer);
            cartItemToSave.setSaleItem(saleItem);
            cartItemToSave.setSeller(seller); // Store the seller reference
            cartItemToSave.setQuantity(requestedQuantity);
        }

        CartItem savedCartItem = cartItemRepository.save(cartItemToSave);

        return CartItemResponseDto.fromEntity(savedCartItem);
    }

    @Transactional
    public CartItemResponseDto updateCartItemQuantity(Long userId, Integer cartItemId, UpdateCartItemQuantityDto updateDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // 2. 🛡️ Authorization Check: Ensure the item belongs to the logged-in user
        if (!cartItem.getUser().getId().equals(userId.intValue())) {
            throw new AccessDeniedException("User does not have permission to modify this cart item.");
        }

        long availableStock = cartItem.getSaleItem().getQuantity();
        int newQuantity = updateDto.getQuantity();

        if (newQuantity > availableStock) {
            throw new InsufficientStockException("Cannot set quantity. Available stock: " + availableStock + ", Requested: " + newQuantity);
        }

        cartItem.setQuantity(newQuantity);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        return CartItemResponseDto.fromEntity(updatedCartItem);
    }

    @Transactional
    public void removeCartItem(Long userId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(userId.intValue())) {
            throw new AccessDeniedException("User does not have permission to remove this cart item.");
        }

        cartItemRepository.delete(cartItem);
    }
}
