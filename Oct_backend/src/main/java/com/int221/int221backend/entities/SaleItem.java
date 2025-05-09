package com.int221.int221backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "quantity",nullable = false)
    private Integer quantity = 1;

    @Column(name = "created_on", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @PrePersist
    protected void onCreate() {
        createdOn = updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
