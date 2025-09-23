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
@Transactional
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
            System.out.println("UploadDir = " + uploadDir);
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

        // ลบไฟล์จาก VM / container
        Path path = Path.of(uploadDir, attachment.getFilename());
        System.out.println("Deleting file at: " + path.toAbsolutePath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // log error แต่ไม่ throw เพื่อไม่ให้ rollback DB
            log.error("Failed to delete file: {}", path.toAbsolutePath(), e);
        }
        attachmentRepository.delete(attachment);
        saleItem.getAttachments().remove(attachment);
    }

    // ลบรูปตาม fileName
    private void deleteImageByFileName(SaleItem saleItem, String fileName) {
        Attachment attachment = saleItem.getAttachments().stream()
                .filter(a -> Objects.equals(a.getFilename(), fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No image with filename: " + fileName));

        Path path = Path.of(uploadDir, attachment.getFilename());
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", path.toAbsolutePath(), e);
        }
        attachmentRepository.delete(attachment);
        saleItem.getAttachments().remove(attachment);
    }

    // อัปเดตรูปภาพโดยเปลี่ยน order
    private void updateImageOrder(SaleItem saleItem, Integer newOrder) {
        if (newOrder == null || newOrder <= 0) return;

        List<Attachment> attachments = saleItem.getAttachments();
        if (attachments.size() < 2) return;

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

            if (saleItemDto.getBrand() != null) {
                Brand brand = brandRepository.findById(saleItemDto.getBrand().getId())
                        .orElseThrow(() -> new NotFoundException("No Brand id = " + saleItemDto.getBrand().getId()));
                existingItem.setBrand(brand);
            }
        }

        if (request.getImagesInfos() != null) {
            // 1) Collect desired orders for existing images (by fileName)
            Map<String, Integer> desiredOrderByFile = new HashMap<>();

            // 2) First pass: handle deletes and adds; collect order mapping for keep/updateOrder
            for (SaleItemImageRequest imgReq : request.getImagesInfos()) {
                String status = imgReq.getStatus();
                if ("delete".equalsIgnoreCase(status)) {
                    String fileName = imgReq.getFileName();
                    if (fileName != null) {
                        deleteImageByFileName(existingItem, fileName);
                    } else {
                        deleteImage(existingItem, imgReq.getOrder());
                    }
                } else if ("add".equalsIgnoreCase(status)) {
                    MultipartFile file = imgReq.getImageFile();
                    if (file != null && !file.isEmpty()) {
                        addNewImage(existingItem, file, imgReq.getOrder());
                    }
                } else if ("keep".equalsIgnoreCase(status) || "updateOrder".equalsIgnoreCase(status)) {
                    if (imgReq.getFileName() != null && imgReq.getOrder() != null) {
                        desiredOrderByFile.put(imgReq.getFileName(), imgReq.getOrder());
                    }
                } else {
                    throw new IllegalArgumentException("Invalid image status: " + status);
                }
            }

            // 3) Apply desired orders to existing attachments by filename
            for (Attachment a : new ArrayList<>(existingItem.getAttachments())) {
                Integer desired = desiredOrderByFile.get(a.getFilename());
                if (desired != null) {
                    a.setImageViewOrder(desired);
                    attachmentRepository.save(a);
                }
            }

            // 4) Normalize orders to 1..N without gaps
            List<Attachment> normalized = existingItem.getAttachments().stream()
                    .sorted(Comparator.comparingInt(Attachment::getImageViewOrder))
                    .collect(Collectors.toList());
            int orderCounter = 1;
            for (Attachment a : normalized) {
                if (a.getImageViewOrder() != orderCounter) {
                    a.setImageViewOrder(orderCounter);
                    attachmentRepository.save(a);
                }
                orderCounter++;
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

    @Transactional
    public void deleteSaleItemById(Integer id) {
        SaleItem saleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No Item id = " + id));

        if (saleItem.getAttachments() != null) {
            for (Attachment attachment : saleItem.getAttachments()) {
                Path filePath = Path.of(uploadDir, attachment.getFilename());
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    log.error("Failed to delete file: {}", filePath, e);
                }
            }
        }

        saleItemRepository.delete(saleItem);
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
        Integer minPrice = null;
        Integer maxPrice = null;

        if (filterPriceLower != null && filterPriceUpper == null) {
            minPrice = filterPriceLower;
            maxPrice = filterPriceLower;
        } else if (filterPriceLower == null && filterPriceUpper != null) {
            minPrice = filterPriceUpper;
            maxPrice = filterPriceUpper;
        } else if (filterPriceLower != null && filterPriceUpper != null) {
            minPrice = filterPriceLower;
            maxPrice = filterPriceUpper;
        }

        Page<SaleItem> saleItemPage = saleItemRepository.findByFiltersAndSearch(
                brandList, storageList, pageable, minPrice, maxPrice, searchKeyWord
        );
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
