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

    @Query("select st from SaleItem st where (:brandList is null or st.brand.name IN :brandList) and (:storageList is null or st.storageGb IN :storageList) " +
            "and (:priceLower is null or st.price >= :priceLower) and (:priceUpper is null or st.price <= :priceUpper)")
    Page<SaleItem> findByBrand_NameIn(@Param("brandList") List<String> brandList,
                                      @Param("storageList") List<Integer> storageList, Pageable pageable,
                                      @Param("priceLower") Integer priceLower,
                                      @Param("priceUpper") Integer priceUpper);

}
