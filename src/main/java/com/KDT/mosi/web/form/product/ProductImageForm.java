package com.KDT.mosi.web.form.product;

import lombok.Data;

import java.sql.Date;

@Data
public class ProductImageForm {
  private Long imageId;
  private Long productId;

  // DB에서 읽은 imageData를 Base64 변환 후 전달하는 필드
  private String base64ImageData;

  private Integer imageOrder;
  private String fileName;
  private Long fileSize;
  private String mimeType;
  private Date uploadTime;
}