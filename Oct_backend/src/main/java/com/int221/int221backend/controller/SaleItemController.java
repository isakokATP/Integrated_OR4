package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.CreateSaleItemRequestDto;
import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemImageInfo;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;

import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import com.int221.int221backend.services.SaleItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/v1/sale-items")
    public List<SaleItemDto> getAllSaleItem() {
        List<SaleItem> saleItemList = saleItemService.getAllSaleItem();
        System.out.println(saleItemList);
        return saleItemList.stream()
                .map(saleItem -> {
                    SaleItemDto saleItemDto = modelMapper.map(saleItem, SaleItemDto.class);
                    // saleItemDto.setBrandName(saleItem.getBrand().getName());
                    saleItemDto.setBrandName(saleItem.getBrand().getName());
                    System.out.println("Mapped SaleItemDTO: " + saleItemDto);
                    return saleItemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/v1/sale-items/{id}")
    public SaleItemByIdDto getItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setBrandName(saleItem.getBrand().getName());
        return saleItemByIdDto;
    }

    //  PBI 10
    @GetMapping("/v2/sale-items")
    public ResponseEntity<SaleItemPaginateDto> getAllItems(@RequestParam(value = "filterBrands", required = false) String[] filterBrands,
                                                           @RequestParam(value = "filterStorages", required = false) Integer[] storageSize,
                                                           @RequestParam(value = "filterPriceLower", required = false) Integer filterPriceLower,
                                                           @RequestParam(value = "filterPriceUpper", required = false) Integer filterPriceUpper,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                           @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                                           @RequestParam(value = "searchKeyWord" , required = false) String searchKeyWord) {
        return ResponseEntity.status(HttpStatus.OK).body(saleItemService.getAllSaleItem(sortDirection, sortField, page, size, filterBrands, storageSize, filterPriceLower, filterPriceUpper, searchKeyWord));
    }

    //pbi15
    @GetMapping("/v2/sale-items/{id}")
    public SaleItemByIdDto getSaleItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found"));

        // map attachments → AttachmentDto
        List<AttachmentDto> images = saleItem.getAttachments().stream()
                .sorted(Comparator.comparingInt(Attachment::getImageViewOrder))
                .map(a -> new AttachmentDto(a.getFilename(), a.getImageViewOrder()))
                .collect(Collectors.toList());

        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setSaleItemImages(images);
        return saleItemByIdDto;
    }

    // pbi15
    @PostMapping("/v2/sale-items")
    public ResponseEntity<NewSaleItemResponseDto> createProduct(
            @ModelAttribute NewSaleItemDto newSaleItem,
            @RequestParam(value = "SaleItemImages", required = false) List<MultipartFile> images
    ) {
        try {
            NewSaleItemResponseDto response = saleItemService.createSaleItem(newSaleItem, images);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create SaleItem", e);
        }
    }

//  pbi16
    @PutMapping("/v2/sale-items/{id}")
    public ResponseEntity<SaleItemUpdateResponseDto> updateSaleItem(
            @PathVariable Integer id,
            @ModelAttribute SaleItemImageInfo request
            ){
        try {
// check at least one or more request to edit
            if (request.getSaleItem() == null &&
                    (request.getImagesInfos() == null || request.getImagesInfos().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Request must contain either saleItem data or imagesInfos (or both).");
            }
            SaleItemUpdateResponseDto response =
                    saleItemService.updateSaleItemWithImages(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update SaleItem", e);
        }
    }

    @DeleteMapping("/v2/sale-items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        try {
            saleItemService.deleteSaleItemById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete SaleItem", e);
        }
    }

    @GetMapping("/v2/sellers/{id}/sale-items")
    public ResponseEntity<?> getSellerSaleItems(
            @PathVariable Long id,
            Pageable pageable,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid or missing token"));
            }

            Long loggedInUserId = jwtTokenProvider.extractId(token);
            String userRole = jwtTokenProvider.extractRole(token);

            if (loggedInUserId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access Denied: Token is missing user ID information."));
            }

            if (!"SELLER".equalsIgnoreCase(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access Denied: User is not a seller."));
            }

            if (!loggedInUserId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access Denied: You can only view your own sale items."));
            }

            SellerSaleItemPaginateDto saleItems = saleItemService.getSaleItemsBySellerId(id.intValue(), pageable);

            return ResponseEntity.ok(saleItems);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/v2/sellers/{id}/sale-items")
    public ResponseEntity<?> createSaleItemForSeller(
            @PathVariable Long id,
            @RequestParam String model,
            @RequestParam Integer brandId,
            @RequestParam String description,
            @RequestParam Integer price,
            @RequestParam(required = false) Integer ramGb,
            @RequestParam(required = false) Integer storageGb,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Long quantity,
            HttpServletRequest request) {

        try {
            authorizeRequest(id, request);

            CreateSaleItemRequestDto createDto = new CreateSaleItemRequestDto();
            createDto.setModel(model);
            createDto.setBrandId(brandId);
            createDto.setDescription(description);
            createDto.setPrice(price);
            createDto.setRamGb(ramGb);
            createDto.setStorageGb(storageGb);
            createDto.setColor(color);
            createDto.setQuantity(quantity);

            SaleItemDetailResponseDto newSaleItem = saleItemService.createSaleItemForSeller(id, createDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(newSaleItem);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // ตัดคำว่า "Bearer " ออก
        }
        return null;
    }

    private void authorizeRequest(Long resourceId, HttpServletRequest request) throws SecurityException {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new SecurityException("401: Invalid or missing token.");
        }

        Long loggedInUserId = jwtTokenProvider.extractId(token);
        String userRole = jwtTokenProvider.extractRole(token);

        if (loggedInUserId == null) {
            throw new SecurityException("403: Token is missing user ID information.");
        }
        if (!"SELLER".equalsIgnoreCase(userRole)) {
            throw new SecurityException("403: Access Denied. User is not a seller.");
        }
        if (!loggedInUserId.equals(resourceId)) {
            throw new SecurityException("403: Access Denied. You can only add items to your own account.");
        }
    }
}