package com.KDT.mosi.domain.mypage.seller.svc;

import com.KDT.mosi.domain.entity.SellerPage;

import java.util.Optional;


/**
 * 판매자 마이페이지 DAO
 */
public interface SellerPageSVC {

  /**
   * 회원 ID로 마이페이지 존재 여부 확인
   *
   * @param memberId 회원 ID
   * @return true: 존재함, false: 없음
   */
  boolean existByMemberId(Long memberId);

  // 등록
  Long save(SellerPage sellerpage);

  // 조회 by 회원 ID
  Optional<SellerPage> findByMemberId(Long memberId);

  // 조회 by 페이지 ID
  Optional<SellerPage> findById(Long pageId);

  // 수정
  int updateById(Long pageId, SellerPage sellerpage);

  // 삭제
  int deleteByMemberId(Long memberId);

  //닉네임 중복 여부 확인
  boolean existByNickname(String nickname);

}
