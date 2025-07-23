package com.KDT.mosi.web.form.product.old;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePointForm {

  private Long coursePointId; // 수정시 사용

  @NotNull(message = "순서 값이 필요합니다.")
  private Integer pointOrder;

  @NotNull(message = "위도 값이 필요합니다.")
  private BigDecimal latitude;

  @NotNull(message = "경도 값이 필요합니다.")
  private BigDecimal longitude;

  @Size(max = 500)
  private String description;
}