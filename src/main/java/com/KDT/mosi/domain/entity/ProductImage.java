package com.KDT.mosi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Base64;

@Entity
@Table(name = "PRODUCT_IMAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_image_seq_generator")
  @SequenceGenerator(name = "product_image_seq_generator", sequenceName = "PRODUCT_IMAGE_IMAGE_ID_SEQ", allocationSize = 1)
  @Column(name = "IMAGE_ID")
  private Long imageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")  // 외래키 컬럼명 맞추기
  private Product product;

  @Lob
  @Column(name = "IMAGE_DATA")
  private byte[] imageData;

  @Column(name = "IMAGE_ORDER")
  private Integer imageOrder;

  @Column(name = "FILE_NAME", length = 255)
  private String fileName;

  @Column(name = "FILE_SIZE")
  private Long fileSize;

  @Column(name = "MIME_TYPE", length = 50)
  private String mimeType;

  @Column(name = "UPLOAD_TIME")
  private Date uploadTime;
  
  // Base64 인코딩된 이미지 데이터 (임시 저장용)
  @Transient
  private String base64ImageData;
  
  // Base64 인코딩 메서드
  public String getBase64ImageData() {
    if (base64ImageData == null && imageData != null) {
      base64ImageData = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageData);
    }
    return base64ImageData;
  }
}