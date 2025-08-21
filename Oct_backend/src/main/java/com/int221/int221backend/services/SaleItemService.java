package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewSaleItemDto;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    @Value("${file.upload-dir}")
    private String uploadDir; // รับค่าจาก application.properties

    public List<SaleItem> getAllSaleItem(){
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No Item id = " + id));   
    }

//    public NewSaleItemResponseDto createSaleItem(NewSaleItemDto newSaleItemDto) {
//        SaleItem saleItem = modelMapper.map(newSaleItemDto, SaleItem.class);
//        if (saleItem.getColor() == null || saleItem.getColor().trim().isEmpty()) {
//            saleItem.setColor(null);
//        }
//
//        if (saleItem.getQuantity() != null && saleItem.getQuantity() < 0L) {
//            throw new IllegalArgumentException("Quantity cannot be negative.");
//        }
//
//        SaleItem savedItem = saleItemRepository.save(saleItem);
//        SaleItem reloadedItem = saleItemRepository.findById(savedItem.getId())
//                .orElseThrow(() -> new RuntimeException("Saved item not found."));
//        return modelMapper.map(reloadedItem, NewSaleItemResponseDto.class);
//    }

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




    @Transactional
    public SaleItemsUpdateResponseDto updateSaleItem(SaleItemsUpdateDto saleItemsUpdateDto) {
        if (saleItemsUpdateDto.getQuantity() == null || saleItemsUpdateDto.getQuantity() < 0L) {
            saleItemsUpdateDto.setQuantity(1L);
        }
        if (saleItemsUpdateDto.getColor() == null || saleItemsUpdateDto.getColor().trim().isEmpty()) {
            saleItemsUpdateDto.setColor(null);
        }
        SaleItem saleItem = modelMapper.map(saleItemsUpdateDto, SaleItem.class);
        Optional<Brand> brand = brandRepository.findById(saleItemsUpdateDto.getBrand().getId());
        if (brand.isPresent()) {
            Brand brandModel = brand.get();
            saleItem.setBrand(brandModel);
        }
        else {
            throw new NotFoundException("No Brand id = " + saleItemsUpdateDto.getBrand().getId());
        }
        SaleItem updatedItem = saleItemRepository.saveAndFlush(saleItem);
        entityManager.refresh(updatedItem);
        return modelMapper.map(updatedItem, SaleItemsUpdateResponseDto.class);
    }

    public void deleteSaleItemById(Integer id) {
        if (saleItemRepository.existsById(id)) {
            saleItemRepository.deleteById(id);
        } else {
            throw new NotFoundException("No Item id = " + id);
        }
    }

    public SaleItemPaginateDto getAllSaleItem(String sortDirection, String sortBy, Integer page, Integer pageSize, String[] filterBrands,
                                              Integer[] storageSize,Integer filterPriceLower, Integer filterPriceUpper) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sort = Sort.by(direction, sortBy).and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<String> brandList = filterBrands == null ? null :  Arrays.asList(filterBrands);
        List<Integer> storageList = storageSize == null ? null : new ArrayList<>(Arrays.asList(storageSize));
        if (storageList != null && storageList.isEmpty()) {
            storageList.add(null);
            System.out.println(storageList.size());
        }

        Page<SaleItem> saleItemPage;


        saleItemPage = saleItemRepository.findByBrand_NameIn(brandList, storageList, pageable, filterPriceLower, filterPriceUpper);

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
