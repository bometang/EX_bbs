package com.KDT.mosi.web.form.product.old;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageForm {

  private Long imageId; // 이미지 수정시 사용, 등록시 null 가능

  @NotNull(message = "이미지 데이터가 필요합니다.")
  private byte[] imageData;

  private Integer imageOrder;

  @Size(max = 255)
  private String fileName;

  private Long fileSize;

  @Size(max = 50)
  private String mimeType;
}