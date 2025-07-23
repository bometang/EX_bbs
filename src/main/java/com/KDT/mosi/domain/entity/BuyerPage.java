package com.KDT.mosi.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuyerPage {

  private Long pageId;                // number(10)       -- 마이페이지 ID
  private Long memberId;              // number(10)       -- 회원 ID (외래키)
  private String nickname;
  private byte[] image;               // blob             -- 프로필 이미지
  private String intro;               // varchar2(500)    -- 자기소개글
  private String recentOrder;         // varchar2(100)    -- 최근 주문 상품명
  private Integer point;              // number(10)       -- 적립 포인트
  private LocalDateTime createDate;   // timestamp        -- 생성일시
  private LocalDateTime updateDate;   // timestamp        -- 수정일시
  private String tel;
  private String name;
  private String address;
  private String zonecode;
  private String detailAddress;
  private String notification;
}
