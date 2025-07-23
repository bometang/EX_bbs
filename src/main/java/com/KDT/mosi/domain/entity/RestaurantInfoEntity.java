package com.KDT.mosi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "RESTAURANT_INFO")
@Data
public class RestaurantInfoEntity {

  @Id
  private Long ucSeq;

  private String mainTitle;
  private String gugunNm;
  private Double lat;
  private Double lng;
  private String place;
  private String title;
  private String subTitle;
  private String addr1;
  private String addr2;
  private String cntctTel;
  private String homepageUrl;
  private String usageDayWeekAndTime;
  private String rprsnTvMenu;
  private String mainImgNormal;
  private String mainImgThumb;
  private String itemCntnts;
}