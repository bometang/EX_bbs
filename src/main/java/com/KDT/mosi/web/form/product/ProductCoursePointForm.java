package com.KDT.mosi.web.form.product;

import lombok.Data;

@Data
public class ProductCoursePointForm {
  private Long coursePointId;
  private Long productId;
  private Integer pointOrder;
  private Double latitude;
  private Double longitude;
  private String description;
}