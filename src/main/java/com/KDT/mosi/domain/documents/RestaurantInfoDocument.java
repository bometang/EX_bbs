package com.KDT.mosi.domain.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "restaurant_info")
public class RestaurantInfoDocument {

  @Id
  private Long ucSeq;

  @Field(type = FieldType.Text)
  private String mainTitle;

  @Field(type = FieldType.Keyword)
  private String gugunNm;

  @Field(type = FieldType.Double)
  private Double lat;

  @Field(type = FieldType.Double)
  private Double lng;

  @Field(type = FieldType.Text)
  private String place;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String subTitle;

  @Field(type = FieldType.Text)
  private String addr1;

  @Field(type = FieldType.Text)
  private String addr2;

  @Field(type = FieldType.Keyword)
  private String cntctTel;

  @Field(type = FieldType.Keyword)
  private String homepageUrl;

  @Field(type = FieldType.Text)
  private String usageDayWeekAndTime;

  @Field(type = FieldType.Text)
  private String rprsnTvMenu;

  @Field(type = FieldType.Keyword)
  private String mainImgNormal;

  @Field(type = FieldType.Keyword)
  private String mainImgThumb;

  @Field(type = FieldType.Text)
  private String itemCntnts;
}