package com.KDT.mosi.domain.product.svc;

import com.KDT.mosi.domain.entity.Product;
import com.KDT.mosi.domain.member.dao.MemberDAO;
import com.KDT.mosi.domain.product.dao.ProductDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductSVCImpl implements ProductSVC {

  private final ProductDAO productDAO;
  private final MemberDAO memberDAO;

  public ProductSVCImpl(ProductDAO productDAO, MemberDAO memberDAO) {
    this.productDAO = productDAO;
    this.memberDAO = memberDAO;
  }

  @Override
  @Transactional
  public Product registerProduct(Product product) {
    Long memberId = extractMemberId(product);
    validateMemberId(memberId);
    return productDAO.insert(product);
  }

  @Override
  @Transactional
  public Product updateProduct(Product product) {
    Long memberId = extractMemberId(product);
    validateMemberId(memberId);
    // 기존 상품 존재 여부 체크 추가 가능
    return productDAO.update(product);
  }

  @Override
  @Transactional
  public void removeProduct(Long productId) {
    productDAO.delete(productId);
  }

  @Override
  public Optional<Product> getProduct(Long productId) {
    return productDAO.findById(productId);
  }

  @Override
  public List<Product> getProductsByMemberIdAndPage(Long memberId, int page, int size){
    return productDAO.findByMemberIdWithPaging(memberId, page, size);
  }

  @Override
  public List<Product> getProductsByMemberIdAndStatusAndPage(Long memberId, String status, int page, int size) {
    // DAO에 적절한 메서드를 호출
    return productDAO.findByMemberIdAndStatusWithPaging(memberId, status, page, size);
  }

  // 카테고리별 상품출력
  @Override
  public List<Product> getProductsByCategoryAndPageAndSize(String category, int page, int size) {
    // DAO에 적절한 메서드를 호출
    return productDAO.findByCategoryWithPaging(category, page, size);
  }

  @Transactional
  public void updateProductStatus(Long productId, String status) {
    Optional<Product> optionalProduct = productDAO.findById(productId);
    if (optionalProduct.isEmpty()) {
      throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
    }

    Product product = optionalProduct.get();
    product.setStatus(status);
    product.setUpdateDate(new Date(System.currentTimeMillis())); // 현재 시간으로 세팅
    productDAO.update(product);
  }

  @Override
  public long countByMemberIdAndStatus(Long memberId, String status) {
    return productDAO.countByMemberIdAndStatus(memberId, status);
  }

  // 카테고리별 상품 갯수
  @Override
  public long countByCategory(String category) {
    // DAO에 적절한 메서드를 호출
    return productDAO.countByCategory(category);
  }

  @Override
  public List<Product> getProductsByPage(int pageNumber, int pageSize) {
    return productDAO.findAllByPage(pageNumber, pageSize);
  }

  @Override
  public long countAllProducts() {
    return productDAO.countAll();
  }

  // Member 객체에서 ID 안전하게 추출
  private Long extractMemberId(Product product) {
    if (product == null || product.getMember() == null) {
      return null;
    }
    return product.getMember().getMemberId();
  }

  // memberId 유효성 검사
  private void validateMemberId(Long memberId) {
    if (memberId == null || !memberDAO.isExistMemberId(memberId)) {
      throw new IllegalArgumentException("존재하지 않는 회원 ID입니다: " + memberId);
    }
  }

  public long countByMemberId(Long memberId){ return productDAO.countByMemberId(memberId); }
}