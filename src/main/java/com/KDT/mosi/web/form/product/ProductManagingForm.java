package com.KDT.mosi.web.form.product;

import com.KDT.mosi.domain.entity.Product;
import com.KDT.mosi.domain.entity.ProductCoursePoint;
import com.KDT.mosi.domain.entity.ProductImage;
import lombok.Data;

import java.util.List;

@Data
public class ProductManagingForm {
  private Product product;
  private List<ProductImage> images;
  private List<ProductCoursePoint> coursePoints;
  private String status;
  private String nickname;
  private String sellerImage;
  private long countProduct;
}