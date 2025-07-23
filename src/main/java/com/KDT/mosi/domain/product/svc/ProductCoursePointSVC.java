package com.KDT.mosi.domain.product.svc;

import com.KDT.mosi.domain.entity.ProductCoursePoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductCoursePointSVC {

  void saveAll(List<ProductCoursePoint> points);

  List<ProductCoursePoint> findByProductId(Long productId);  // 추가

  void deleteByProductId(Long productId);

  List<ProductCoursePoint> getPointsByProductId(Long productId);

  boolean addCoursePoint(ProductCoursePoint point);

  boolean deleteCoursePoint(Long coursePointId);
}