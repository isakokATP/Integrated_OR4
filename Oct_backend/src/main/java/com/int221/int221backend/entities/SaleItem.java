package com.int221.int221backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@Table(name = "sale_items", schema = "pbi1")
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "model", length = 60, nullable = false)
    private String model;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(name = "screen_size_inch", precision = 4, scale = 2)
    private BigDecimal screenSizeInch;

    @Column(name = "storage_gb")
    private Integer storageGb;

    @Column(name = "color", length = 30, nullable = true)
    private String color;

    @Column(name = "quantity",nullable = false)
    private Integer quantity = 1;

    @Column(name = "created_on", nullable = false, insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", nullable = false, insertable = false, updatable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
