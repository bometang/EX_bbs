package com.KDT.mosi.web.form.mypage.buyerpage;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuyerPageDetailForm {

  // 마이페이지 고유 ID
  private Long pageId;

  // 회원 ID
  private Long memberId;

  // 프로필 이미지 (base64 또는 URL로 변환해서 출력)
  private byte[] image;

  // 자기소개
  private String intro;

  // 최근 주문 상품명
  private String recentOrder;

  // 포인트
  private Integer point;

  // 생성일시 (읽기 전용)
  private LocalDateTime createDate;

  // 마지막 수정일시 (읽기 전용)
  private LocalDateTime updateDate;
}

