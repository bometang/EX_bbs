package com.KDT.mosi.domain.product.svc;

import com.KDT.mosi.domain.entity.ProductImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductImageSVC {
  void saveAll(List<ProductImage> images);

  List<ProductImage> findByProductId(Long productId);  // 추가

  void deleteByProductId(Long productId);

  // 기존에 있던 컨트롤러 호환용 메서드도 필요하다면 같이 유지
  List<ProductImage> getImagesByProductId(Long productId);
  boolean addProductImage(ProductImage productImage);
  boolean deleteProductImage(Long imageId);

  Optional<ProductImage> findById(Long imageId);
}