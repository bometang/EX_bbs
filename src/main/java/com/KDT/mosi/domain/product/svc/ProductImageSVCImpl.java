package com.KDT.mosi.domain.product.svc;

import com.KDT.mosi.domain.entity.ProductImage;
import com.KDT.mosi.domain.product.dao.ProductImageDAO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageSVCImpl implements ProductImageSVC {

  private final ProductImageDAO productImageDAO;

  @Override
  public void saveAll(List<ProductImage> images) {
    for (ProductImage image : images) {
      productImageDAO.insert(image);
    }
  }

  @Override
  public List<ProductImage> findByProductId(Long productId) {
    return productImageDAO.findByProductId(productId);
  }

  @Override
  public void deleteByProductId(Long productId) {
    productImageDAO.deleteByProductId(productId);
  }

  // getImagesByProductId 와 기능 중복, 필요 시 삭제 가능
  @Override
  public List<ProductImage> getImagesByProductId(Long productId) {
    return findByProductId(productId);
  }

  @Override
  public boolean addProductImage(ProductImage productImage) {
    return productImageDAO.insert(productImage) > 0;
  }

  @Override
  public boolean deleteProductImage(Long imageId) {
    return productImageDAO.delete(imageId) > 0;
  }

  @Override
  public Optional<ProductImage> findById(Long imageId) {
    return productImageDAO.findById(imageId);
  }
}