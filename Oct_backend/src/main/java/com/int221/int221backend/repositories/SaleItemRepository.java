package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository <SaleItem, Integer> {
    Integer countSaleItemByBrand_Id(Integer brandId);

    Page<SaleItem> findAllBySeller_Id(Integer sellerId, Pageable pageable);

    @Query("select st from SaleItem st " +
            "where (:brandList is null or st.brand.name IN :brandList) " +
            "and (:storageList is null or st.storageGb IN :storageList or (-1 in (:storageList) and st.storageGb is null)) " +
            "and (:priceLower is null or st.price >= :priceLower) " +
            "and (:priceUpper is null or st.price <= :priceUpper)")
    Page<SaleItem> findByBrand_NameIn(@Param("brandList") List<String> brandList,
                                      @Param("storageList") List<Integer> storageList, Pageable pageable,
                                      @Param("priceLower") Integer priceLower,
                                      @Param("priceUpper") Integer priceUpper);

    @Query("""
        SELECT s FROM SaleItem s
        WHERE (:brands IS NULL OR s.brand.name IN :brands)
          AND (:storageSizes IS NULL OR s.storageGb IN :storageSizes)
          AND (:minPrice IS NULL OR s.price >= :minPrice)
          AND (:maxPrice IS NULL OR s.price <= :maxPrice)
          AND (:search IS NULL OR LOWER(s.model) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<SaleItem> findByFiltersAndSearch(
            @Param("brands") List<String> brands,
            @Param("storageSizes") List<Integer> storageSizes,
            Pageable pageable,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("search") String search
    );
}
