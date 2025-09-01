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

    @Query("select st from SaleItem st " +
            "where (:brandList is null or st.brand.name IN :brandList) " +
            "and (:storageList is null or st.storageGb IN :storageList or (-1 in (:storageList) and st.storageGb is null)) " +
            "and (:priceLower is null or st.price >= :priceLower) " +
            "and (:priceUpper is null or st.price <= :priceUpper)")
    Page<SaleItem> findByBrand_NameIn(@Param("brandList") List<String> brandList,
                                      @Param("storageList") List<Integer> storageList, Pageable pageable,
                                      @Param("priceLower") Integer priceLower,
                                      @Param("priceUpper") Integer priceUpper);

    @Query("SELECT st FROM SaleItem st " +
            "WHERE (:brandList IS NULL OR st.brand.name IN :brandList) " +
            "AND (:storageList IS NULL OR st.storageGb IN :storageList OR (-1 IN (:storageList) AND st.storageGb IS NULL)) " +
            "AND (:priceLower IS NULL OR st.price >= :priceLower) " +
            "AND (:priceUpper IS NULL OR st.price <= :priceUpper) " +
            "AND (" +
            "    :searchKeyWord IS NULL OR " +
            "    LOWER(REPLACE(st.description, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:searchKeyWord, ' ', ''), '%')) OR " +
            "    LOWER(REPLACE(st.model, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:searchKeyWord, ' ', ''), '%')) OR " +
            "    LOWER(REPLACE(st.color, ' ', '')) LIKE LOWER(CONCAT('%', REPLACE(:searchKeyWord, ' ', ''), '%'))" +
            ")")
    Page<SaleItem> findByFiltersAndSearch(
            @Param("brandList") List<String> brandList,
            @Param("storageList") List<Integer> storageList,
            Pageable pageable,
            @Param("priceLower") Integer priceLower,
            @Param("priceUpper") Integer priceUpper,
            @Param("searchKeyWord") String searchKeyWord
    );
}
