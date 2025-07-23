package com.KDT.mosi.web.form.mypage.sellerpage;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerPageDetailForm {

  // 셀러 페이지 고유 ID
  private Long pageId;

  // 회원 ID
  private Long memberId;

  // 프로필 이미지 (base64 또는 URL로 변환해서 출력)
  private byte[] image;

  // 자기소개
  private String intro;

  // 별명
  private String nickname;

  // 판매 상품명 (최근 판매 상품 등)
  private String recentProduct;

  // 누적 판매량 또는 판매 지수
  private Integer salesCount;

  // 생성일시 (읽기 전용)
  private LocalDateTime createDate;

  // 마지막 수정일시 (읽기 전용)
  private LocalDateTime updateDate;
}
