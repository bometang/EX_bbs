package com.KDT.mosi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "PRODUCT_COURSE_POINT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCoursePoint {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_point_seq_generator")
  @SequenceGenerator(name = "course_point_seq_generator", sequenceName = "PRODUCT_COURSE_POINT_COURSE_POINT_ID_SEQ", allocationSize = 1)
  @Column(name = "COURSE_POINT_ID")
  private Long coursePointId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")  // 외래키 컬럼명 맞추기
  private Product product;

  @Column(name = "POINT_ORDER")
  private Integer pointOrder;

  @Column(name = "LATITUDE")
  private Double latitude;

  @Column(name = "LONGITUDE")
  private Double longitude;

  @Column(name = "DESCRIPTION", length = 500)
  private String description;

  @Column(name = "CREATED_AT")
  private Date createdAt;
}