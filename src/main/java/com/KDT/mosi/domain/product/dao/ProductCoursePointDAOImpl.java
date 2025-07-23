package com.KDT.mosi.domain.product.dao;

import com.KDT.mosi.domain.entity.Product;
import com.KDT.mosi.domain.entity.ProductCoursePoint;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductCoursePointDAOImpl implements ProductCoursePointDAO {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public ProductCoursePointDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<ProductCoursePoint> rowMapper = (rs, rowNum) -> {
    ProductCoursePoint pcp = new ProductCoursePoint();
    pcp.setCoursePointId(rs.getLong("COURSE_POINT_ID"));

    // Product 객체 생성 후 id 세팅
    Long productId = rs.getLong("PRODUCT_ID");
    Product product = new Product();
    product.setProductId(productId);
    pcp.setProduct(product);

    pcp.setPointOrder(rs.getInt("POINT_ORDER"));
    pcp.setLatitude(rs.getDouble("LATITUDE"));
    pcp.setLongitude(rs.getDouble("LONGITUDE"));
    pcp.setDescription(rs.getString("DESCRIPTION"));
    pcp.setCreatedAt(rs.getDate("CREATED_AT"));
    return pcp;
  };

  @Override
  public List<ProductCoursePoint> findByProductId(Long productId) {
    String sql = "SELECT * FROM PRODUCT_COURSE_POINT WHERE PRODUCT_ID = :productId ORDER BY POINT_ORDER";
    Map<String, Object> params = new HashMap<>();
    params.put("productId", productId);
    return jdbcTemplate.query(sql, params, rowMapper);
  }

  @Override
  public int insert(ProductCoursePoint point) {
    String sql = "INSERT INTO PRODUCT_COURSE_POINT " +
        "(COURSE_POINT_ID, PRODUCT_ID, POINT_ORDER, LATITUDE, LONGITUDE, DESCRIPTION, CREATED_AT) VALUES " +
        "(COURSE_COURSE_ID_SEQ.NEXTVAL, :productId, :pointOrder, :latitude, :longitude, :description, SYSDATE)";
    Map<String, Object> params = new HashMap<>();

    // product 객체로부터 productId 추출
    if (point.getProduct() == null || point.getProduct().getProductId() == null) {
      throw new IllegalArgumentException("Product or productId is null in ProductCoursePoint");
    }
    params.put("productId", point.getProduct().getProductId());

    params.put("pointOrder", point.getPointOrder());
    params.put("latitude", point.getLatitude());
    params.put("longitude", point.getLongitude());
    params.put("description", point.getDescription());
    return jdbcTemplate.update(sql, params);
  }

  @Override
  public int delete(Long coursePointId) {
    String sql = "DELETE FROM PRODUCT_COURSE_POINT WHERE COURSE_POINT_ID = :coursePointId";
    Map<String, Object> params = new HashMap<>();
    params.put("coursePointId", coursePointId);
    return jdbcTemplate.update(sql, params);
  }

  @Override
  public int deleteByProductId(Long productId) {
    String sql = "DELETE FROM PRODUCT_COURSE_POINT WHERE PRODUCT_ID = :productId";
    Map<String, Object> params = new HashMap<>();
    params.put("productId", productId);
    return jdbcTemplate.update(sql, params);
  }
}