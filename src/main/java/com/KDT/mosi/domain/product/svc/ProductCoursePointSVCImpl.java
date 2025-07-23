package com.KDT.mosi.domain.product.svc;

import com.KDT.mosi.domain.entity.ProductCoursePoint;
import com.KDT.mosi.domain.product.dao.ProductCoursePointDAO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCoursePointSVCImpl implements ProductCoursePointSVC {

  private final ProductCoursePointDAO productCoursePointDAO;

  @Override
  public void saveAll(List<ProductCoursePoint> points) {
    for (ProductCoursePoint point : points) {
      if (point.getProduct() == null || point.getProduct().getProductId() == null) {
        throw new IllegalArgumentException("Product or productId is null in ProductCoursePoint");
      }
      productCoursePointDAO.insert(point);
    }
  }

  @Override
  public List<ProductCoursePoint> findByProductId(Long productId) {
    return productCoursePointDAO.findByProductId(productId);
  }

  @Override
  public void deleteByProductId(Long productId) {
    productCoursePointDAO.deleteByProductId(productId);
  }

  // 필요에 따라 getPointsByProductId 메서드 삭제 가능 (중복)
  @Override
  public List<ProductCoursePoint> getPointsByProductId(Long productId) {
    return productCoursePointDAO.findByProductId(productId);
  }

  @Override
  public boolean addCoursePoint(ProductCoursePoint point) {
    if (point.getProduct() == null || point.getProduct().getProductId() == null) {
      throw new IllegalArgumentException("Product or productId is null in ProductCoursePoint");
    }
    return productCoursePointDAO.insert(point) > 0;
  }

  @Override
  public boolean deleteCoursePoint(Long coursePointId) {
    return productCoursePointDAO.delete(coursePointId) > 0;
  }
}