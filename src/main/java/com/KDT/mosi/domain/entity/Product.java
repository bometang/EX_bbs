package com.KDT.mosi.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long productId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")  // DB 컬럼명에 맞게 설정
  private Member member;

  @Column(length = 30, nullable = false)
  private String category;

  @Column(length = 90, nullable = false)
  private String title;

  @Column(name = "guide_yn", length = 1, nullable = false)
  private String guideYn;

  @Column(name = "normal_price", nullable = false)
  private Integer normalPrice;

  @Column(name = "guide_price", nullable = false)
  private Integer guidePrice;

  @Column(name = "sales_price", nullable = false)
  private Integer salesPrice;

  @Column(name = "sales_guide_price", nullable = false)
  private Integer salesGuidePrice;

  @Column(name = "total_day", nullable = false)
  private Integer totalDay;

  @Column(name = "total_time", nullable = false)
  private Integer totalTime;

  @Column(name = "req_money", nullable = false)
  private Integer reqMoney;

  @Column(name = "sleep_info", length=1, nullable=false)
  private String sleepInfo;

  @Column(name = "transport_info", length=45)
  private String transportInfo;

  @Column(name = "food_info", length=1, nullable=false)
  private String foodInfo;

  @Column(name = "req_people", nullable=false)
  private String reqPeople;

  @Column(name = "target", nullable=false)
  private String target;

  @Column(length=90)
  private String stucks;

  @Column(length=1500, nullable=false)
  private String description;

  @Column(length=3000, nullable=false)
  private String detail;

  @Column(name = "file_name", length=255, nullable=false)
  private String fileName;

  @Column(name = "file_type", length=50, nullable=false)
  private String fileType;

  @Column(name = "file_size", nullable=false)
  private Long fileSize;

  @Lob
  @Column(name="file_data", nullable=false)
  private byte[] fileData;

  @Column(name="price_detail", length=450, nullable=false)
  private String priceDetail;

  @Column(name="gprice_detail", length=450, nullable=false)
  private String gpriceDetail;

  @Column(length=12, nullable=false)
  private String status;

  @Temporal(TemporalType.DATE)
  @Column(name="create_date", nullable=false)
  private Date createDate;

  @Temporal(TemporalType.DATE)
  @Column(name="update_date")
  private Date updateDate;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductImage> productImages;
}