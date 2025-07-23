package com.KDT.mosi.domain.product.dao;

import com.KDT.mosi.domain.entity.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageDAO {
  List<ProductImage> findByProductId(Long productId);
  int insert(ProductImage productImage);
  int delete(Long imageId);
  int deleteByProductId(Long productId);

  Optional<ProductImage> findById(Long imageId);
}