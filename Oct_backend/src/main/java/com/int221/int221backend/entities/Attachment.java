package com.int221.int221backend.entities;

import com.int221.int221backend.enums.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.ColumnTransformer;


import java.sql.Timestamp;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attachments", schema = "pbi1")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "saleItem_id", nullable = false)
    private SaleItem saleItem;


    @NotBlank
    @Column(name = "filename", length = 255, nullable = false)
    private String filename;

    @NotBlank
    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;


    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "UPPER(file_type)")
    @Column(name = "file_type", nullable = false)
    private FileType fileType; // ENUM('jpg', 'jpeg', 'png')

    @NotNull
    @Column(name = "file_size")
    private Integer fileSize;

    @NotNull
    @Column(name = "image_view_order")
    private Integer imageViewOrder;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "created_on", insertable = false, updatable = false, nullable = false)
    private Timestamp createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "updated_on", insertable = false, updatable = false, nullable = false)
    private Timestamp updatedOn;

    public Attachment(String filename, String filePath) {
        this.filename = filename;
        this.filePath = filePath;
    }
}
