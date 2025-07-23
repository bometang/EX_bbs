package com.KDT.mosi.domain.mypage.buyer.dao;

import com.KDT.mosi.domain.entity.BuyerPage;

import java.util.Optional;

public interface BuyerPageDAO {

  /**
   * 마이페이지 저장
   * @param buyerPage 마이페이지 정보
   * @return 생성된 페이지 ID
   */
  Long save(BuyerPage buyerPage);

  /**
   * 회원 ID로 마이페이지 조회
   * @param memberId 회원 ID
   * @return Optional<BuyerPage>
   */
  Optional<BuyerPage> findByMemberId(Long memberId);

  /**
   * 마이페이지 수정
   * @param pageId 페이지 ID
   * @param buyerPage 수정할 데이터
   * @return 수정 건수
   */
  int updateById(Long pageId, BuyerPage buyerPage);

  /**
   * 회원 ID로 마이페이지 삭제
   * - 회원 탈퇴 시 사용
   * @param memberId 회원 ID
   * @return 삭제된 행 수
   */
  int deleteByMemberId(Long memberId);

  /**
   * 마이페이지 ID로 조회
   * @param pageId 마이페이지 ID
   * @return Optional<BuyerPage>
   */
  Optional<BuyerPage> findById(Long pageId);
}
