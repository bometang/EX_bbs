package com.KDT.mosi.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SellerPage {
  private Long pageId;             // 마이페이지 ID
  private Long memberId;           // 회원 ID
  private byte[] image;            // 프로필 이미지 (BLOB)
  private String intro;            // 자기소개
  private String nickname;         // 닉네임
  private Integer salesCount;      // 누적 판매 수
  private Integer totalSales;      // 누적 매출액
  private Double reviewAvg;        // 평균 평점
  private Integer reviewCount;     // 리뷰 수
  private Integer recentOrderCnt;  // 최근 1주 주문 수
  private Integer recentQnaCnt;    // 최근 1주 문의 수
  private Integer followerCount;   // 팔로워 수
  private Integer productCount;    // 등록 상품 수
  private String category;         // 주력 판매 카테고리
  private String bankAccount;      // 정산 계좌 정보
  private String snsLink;          // SNS 주소
  private String isActive;         // 활동 여부 (Y/N)
  private Timestamp createDate;    // 생성일
  private Timestamp updateDate;    // 수정일
  private String tel;              // 전화번호
  private String zonecode;         // 우편번호
  private String address;          // 기본 주소
  private String detailAddress;    // 상세 주소
}

