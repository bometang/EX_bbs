package com.KDT.mosi.web.form.product.old;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailForm {

  private Long productId;

  private Long memberId;

  private String name;

  private String category;

  private Integer price;

  private String status;

  private String description;

  private String detail;

  private String guideYn;

  private Integer reqMoney;

  private Integer reqPeople;

  private Integer age;

  private String foodInfo;

  private String sleepInfo;

  private String storeInfo;

  private String promoYn;

  private String transportInfo;

  private LocalDateTime createDate;

  private LocalDateTime updateDate;
}