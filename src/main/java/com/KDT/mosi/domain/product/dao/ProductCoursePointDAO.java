package com.KDT.mosi.domain.product.dao;

import com.KDT.mosi.domain.entity.ProductCoursePoint;

import java.util.List;

public interface ProductCoursePointDAO {
  List<ProductCoursePoint> findByProductId(Long productId);
  int insert(ProductCoursePoint point);
  int delete(Long coursePointId);
  int deleteByProductId(Long productId);
}