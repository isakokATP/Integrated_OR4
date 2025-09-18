package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemImageInfo;
import com.int221.int221backend.dto.request.SaleItemImageRequest;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;
import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.enums.FileType;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.AttachmentRepository;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.utils.ListMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Value("${file.upload-dir.saleitems}")
    private String uploadDir;

    public List<SaleItem> getAllSaleItem(){
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No Item id = " + id));   
    }

//    pbi15 upload picture
    @Transactional
    public NewSaleItemResponseDto createSaleItem(NewSaleItemDto newSaleItemDto, List<MultipartFile> images) {

        if (images != null && images.size() > 4) {
            throw new IllegalArgumentException("You can upload maximum 4 images.");
        }
        // Map DTO -> Entity
        SaleItem saleItem = modelMapper.map(newSaleItemDto, SaleItem.class);

        if (saleItem.getColor() == null || saleItem.getColor().trim().isEmpty()) {
            saleItem.setColor(null);
        }

        if (saleItem.getQuantity() != null && saleItem.getQuantity() < 0L) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }


        // บันทึก SaleItem
        SaleItem savedItem = saleItemRepository.save(saleItem);

        // ตรวจสอบและบันทึกไฟล์รูป
        if (images != null && !images.isEmpty()) {
            int order = 1;
            for (MultipartFile file : images) {

                // 1. ตรวจสอบขนาดไฟล์ ≤ 2MB
                long maxSize = 2 * 1024 * 1024; // 2MB
                if (file.getSize() > maxSize) {
                    throw new IllegalArgumentException(
                            "File " + file.getOriginalFilename() + " exceeds maximum allowed size of 2MB");
                }

                // 2. ตรวจสอบ FileType JPEG / PNG (ignore case)
                String extension = getFileExtension(file.getOriginalFilename());
                if (!extension.equalsIgnoreCase("jpg") &&
                        !extension.equalsIgnoreCase("jpeg") &&
                        !extension.equalsIgnoreCase("png")) {
                    throw new IllegalArgumentException(
                            "File " + file.getOriginalFilename() + " must be JPEG or PNG");
                }

                // สร้าง Path จาก uploadDir
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Path.of(uploadDir, fileName);
                try {
                    Files.createDirectories(path.getParent());
                    Files.write(path, file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
                }

                // สร้าง Attachment Entity
                Attachment attachment = Attachment.builder()
                        .saleItem(savedItem)
                        .filename(fileName)
                        .filePath(path.toString())
                        .fileType(getFileType(extension))
                        .fileSize((int) file.getSize())
                        .imageViewOrder(order++)
                        .build();

                attachmentRepository.save(attachment);
            }
        }

        // โหลด SaleItem พร้อม Attachment
        SaleItem reload = saleItemRepository.saveAndFlush(saleItem);
        entityManager.refresh(reload);
        modelMapper.map(reload, NewSaleItemResponseDto.class);

        List<AttachmentDto> imageDtos = reload.getAttachments().stream()
                .sorted(Comparator.comparingInt(Attachment::getImageViewOrder))
                .map(a -> new AttachmentDto(a.getFilename(), a.getImageViewOrder()))
                .toList();

      // Map SaleItem → DTO
        NewSaleItemResponseDto responseDto = modelMapper.map(reload, NewSaleItemResponseDto.class);
        responseDto.setSaleItemImages(imageDtos);

        return responseDto;
    }

    // Helper ดึงนามสกุลไฟล์
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // Helper แปลง extension เป็น FileType enum
    private FileType getFileType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg": return FileType.JPG;
            case "jpeg": return FileType.JPEG;
            case "png": return FileType.PNG;
            default: throw new IllegalArgumentException("Unsupported file type: " + extension);
        }
    }

//    @Transactional
//    public SaleItemsUpdateResponseDto updateSaleItem(SaleItemsUpdateDto saleItemsUpdateDto) {
//        if (saleItemsUpdateDto.getQuantity() == null || saleItemsUpdateDto.getQuantity() < 0L) {
//            saleItemsUpdateDto.setQuantity(1L);
//        }
//        if (saleItemsUpdateDto.getColor() == null || saleItemsUpdateDto.getColor().trim().isEmpty()) {
//            saleItemsUpdateDto.setColor(null);
//        }
//        SaleItem saleItem = modelMapper.map(saleItemsUpdateDto, SaleItem.class);
//        Optional<Brand> brand = brandRepository.findById(saleItemsUpdateDto.getBrand().getId());
//        if (brand.isPresent()) {
//            Brand brandModel = brand.get();
//            saleItem.setBrand(brandModel);
//        }
//        else {
//            throw new NotFoundException("No Brand id = " + saleItemsUpdateDto.getBrand().getId());
//        }
//        SaleItem updatedItem = saleItemRepository.saveAndFlush(saleItem);
//        entityManager.refresh(updatedItem);
//        return modelMapper.map(updatedItem, SaleItemsUpdateResponseDto.class);
//    }

    private void addNewImage(SaleItem saleItem, MultipartFile file, Integer order) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            if (!extension.equalsIgnoreCase("jpg") &&
                    !extension.equalsIgnoreCase("jpeg") &&
                    !extension.equalsIgnoreCase("png")) {
                throw new IllegalArgumentException("File must be JPEG or PNG");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Path.of(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Attachment attachment = Attachment.builder()
                    .saleItem(saleItem)
                    .filename(fileName)
                    .filePath(fileName)
                    .fileType(getFileType(extension))
                    .fileSize((int) file.getSize())
                    .imageViewOrder(order != null ? order : saleItem.getAttachments().size() + 1)
                    .build();

            attachmentRepository.save(attachment);
            saleItem.getAttachments().add(attachment);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
        }
    }

    // ลบรูปตาม order
    private void deleteImage(SaleItem saleItem, Integer order) {
        Attachment attachment = saleItem.getAttachments().stream()
                .filter(a -> a.getImageViewOrder() == order)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No image with order: " + order));

        // ลบไฟล์จาก VM
        Path path = Path.of(uploadDir, attachment.getFilePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + attachment.getFilePath(), e);
        }
        attachmentRepository.delete(attachment);
        saleItem.getAttachments().remove(attachment);
    }

    // อัปเดตรูปภาพโดยเปลี่ยน order
    private void updateImageOrder(SaleItem saleItem, Integer newOrder) {
        // สมมติคุณส่ง newOrder = 1 และต้องการสลับรูปกับ order 1 ปัจจุบัน
        if (newOrder == null || newOrder <= 0) return;

        // ตัวอย่าง: swap first attachment กับ newOrder
        List<Attachment> attachments = saleItem.getAttachments();
        if (attachments.size() < 2) return;

        // ตัวอย่าง swap logic
        Attachment first = attachments.get(0);
        Attachment target = attachments.stream()
                .filter(a -> a.getImageViewOrder() == newOrder)
                .findFirst()
                .orElse(null);

        if (target != null) {
            int temp = first.getImageViewOrder();
            first.setImageViewOrder(target.getImageViewOrder());
            target.setImageViewOrder(temp);
            attachmentRepository.save(first);
            attachmentRepository.save(target);
        }
    }

    @Transactional
    public SaleItemUpdateResponseDto updateSaleItemWithImages(Integer id, SaleItemImageInfo request) {
        SaleItem existingItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No SaleItem id = " + id));
        SaleItemsUpdateDto saleItemDto = request.getSaleItem();
        if (saleItemDto != null) {
            if (saleItemDto.getQuantity() == null || saleItemDto.getQuantity() < 0L) {
                saleItemDto.setQuantity(1L);
            }
            existingItem.setModel(saleItemDto.getModel());
            existingItem.setDescription(saleItemDto.getDescription());
            existingItem.setPrice(saleItemDto.getPrice());
            existingItem.setQuantity(saleItemDto.getQuantity());
            existingItem.setColor(saleItemDto.getColor());
            existingItem.setRamGb(saleItemDto.getRamGb());
            existingItem.setScreenSizeInch(saleItemDto.getScreenSizeInch());
            existingItem.setStorageGb(saleItemDto.getStorageGb());

            // อัพเดต brand
            if (saleItemDto.getBrand() != null) {
                Brand brand = brandRepository.findById(saleItemDto.getBrand().getId())
                        .orElseThrow(() -> new NotFoundException("No Brand id = " + saleItemDto.getBrand().getId()));
                existingItem.setBrand(brand);
            }
        }

        if (request.getImagesInfos() != null) {
            for (SaleItemImageRequest imgReq : request.getImagesInfos()) {
                switch (imgReq.getStatus()) {
                    case "add" -> {
                        MultipartFile file = imgReq.getImageFile();
                        if (file != null && !file.isEmpty()) {
                            addNewImage(existingItem, file, imgReq.getOrder());
                        }
                    }
                    case "delete" -> {
                        deleteImage(existingItem, imgReq.getOrder());
                    }
                    case "updateOrder" -> {
                        updateImageOrder(existingItem, imgReq.getOrder());
                    }
                    case "keep" -> {
                    }
                    default -> throw new IllegalArgumentException("Invalid image status: " + imgReq.getStatus());
                }
            }
        }

        SaleItem updated = saleItemRepository.saveAndFlush(existingItem);
        entityManager.refresh(updated);

        List<AttachmentDto> imageDtos = updated.getAttachments().stream()
                .sorted(Comparator.comparingInt(Attachment::getImageViewOrder))
                .map(a -> new AttachmentDto(a.getFilename(), a.getImageViewOrder()))
                .toList();

        SaleItemUpdateResponseDto response = modelMapper.map(updated, SaleItemUpdateResponseDto.class);
        response.setSaleItemImages(imageDtos);

        return response;
    }

//    สำหรับ delete ทั้ง item
//    public void deleteSaleItemById(Integer id) {
//        SaleItem saleItem = saleItemRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("No Item id = " + id));
//        if (saleItem.getAttachments() != null && !saleItem.getAttachments().isEmpty()) {
//            for (Attachment attachment : saleItem.getAttachments()) {
//                Path filePath = Path.of(uploadDir, attachment.getFilename());
//                try {
//                    Files.deleteIfExists(filePath); // ลบไฟล์จริง
//                    log.info("Deleted file: {}", filePath);
//                } catch (IOException e) {
//                    log.error("Failed to delete file: {}", filePath, e);
//                    throw new RuntimeException("Failed to delete file: " + filePath, e);
//                }
//            }
//        }
//        saleItemRepository.delete(saleItem);
//    }

    @Transactional
    public void deleteSaleItemById(Integer id) {
        SaleItem item = saleItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No Item id = " + id));

        for (Attachment att : item.getAttachments()) {
            try {
                Path path = Path.of(uploadDir, att.getFilePath());
                Files.deleteIfExists(path);  // delete file ถ้ามี
            } catch (IOException e) {
                log.warn("Cannot delete file: {}", att.getFilePath(), e);
            }
        }

        attachmentRepository.deleteAll(item.getAttachments());

        saleItemRepository.delete(item);
    }

    public SaleItemPaginateDto getAllSaleItem(String sortDirection, String sortBy, Integer page, Integer pageSize, String[] filterBrands,
                                              Integer[] storageSize,Integer filterPriceLower, Integer filterPriceUpper, String searchKeyWord) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sort = Sort.by(direction, sortBy).and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<String> brandList = filterBrands == null ? null :  Arrays.asList(filterBrands);
        List<Integer> storageList = storageSize == null ? null : new ArrayList<>(Arrays.asList(storageSize));
        if (storageList != null && storageList.isEmpty()) {
            storageList.add(null);
            System.out.println(storageList.size());
        }
//        if (filterPriceLower != null && filterPriceUpper == null) {
//            filterPriceUpper = filterPriceLower;
//        }
        Integer minPrice = null;
        Integer maxPrice = null;

        if (filterPriceLower != null && filterPriceUpper == null) {
            // ใช้ราคาตัวเดียว = priceLower
            minPrice = filterPriceLower;
            maxPrice = filterPriceLower;
        } else if (filterPriceLower == null && filterPriceUpper != null) {
            // ใช้ราคาตัวเดียว = priceUpper
            minPrice = filterPriceUpper;
            maxPrice = filterPriceUpper;
        } else if (filterPriceLower != null && filterPriceUpper != null) {
            // ช่วงราคา
            minPrice = filterPriceLower;
            maxPrice = filterPriceUpper;
        }
        // ถ้า minPrice/maxPrice เป็น null จะไม่กรองราคา

        Page<SaleItem> saleItemPage = saleItemRepository.findByFiltersAndSearch(
                brandList, storageList, pageable, minPrice, maxPrice, searchKeyWord
        );

//        Page<SaleItem> saleItemPage;

        SaleItemPaginateDto response = new SaleItemPaginateDto();
        response.setContent(listMapper.mapList(saleItemPage.getContent(), SaleItemByIdDto.class, modelMapper));
        response.setLast(saleItemPage.isLast());
        response.setFirst(saleItemPage.isFirst());
        response.setTotalPages(saleItemPage.getTotalPages());
        response.setTotalElements(saleItemPage.getNumberOfElements());
        response.setSize(saleItemPage.getSize());
        response.setSort(String.format("%s : %s", sortBy, sortDirection));
        response.setPage(page);
        return response;
    }
}
