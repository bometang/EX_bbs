package com.KDT.mosi.domain.mypage.buyer.svc;

import com.KDT.mosi.domain.entity.BuyerPage;

import java.util.Optional;

public interface BuyerPageSVC {

  /**
   * 마이페이지 등록
   * @param buyerPage 등록할 데이터
   * @return 생성된 pageId
   */
  Long create(BuyerPage buyerPage);

  /**
   * 회원ID로 마이페이지 조회
   * @param memberId 회원 ID
   * @return Optional<BuyerPage>
   */
  Optional<BuyerPage> findByMemberId(Long memberId);

  /**
   * 마이페이지 수정
   * @param pageId 수정할 페이지 ID
   * @param buyerPage 수정 내용
   * @return 수정된 건수
   */
  int update(Long pageId, BuyerPage buyerPage);


  /**
   * 마이페이지 삭제 (회원 ID 기준)
   * - 회원 탈퇴 시 사용
   *
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
