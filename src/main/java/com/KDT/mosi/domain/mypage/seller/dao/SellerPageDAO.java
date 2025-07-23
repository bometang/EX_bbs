package com.KDT.mosi.domain.mypage.seller.dao;

import com.KDT.mosi.domain.entity.SellerPage;

import java.util.Optional;

public interface SellerPageDAO {

  /**
   * 판매자 마이페이지 저장
   * @param sellerPage 판매자 마이페이지 정보
   * @return 생성된 페이지 ID
   */
  Long save(SellerPage sellerPage);

  /**
   * 회원 ID로 마이페이지 조회
   * @param memberId 회원 ID
   * @return Optional<SellerPage>
   */
  Optional<SellerPage> findByMemberId(Long memberId);

  /**
   * 마이페이지 수정
   * @param pageId 페이지 ID
   * @param sellerPage 수정할 데이터
   * @return 수정 건수
   */
  int updateById(Long pageId, SellerPage sellerPage);

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
   * @return Optional<SellerPage>
   */
  Optional<SellerPage> findById(Long pageId);

  /**
   * 판매자 페이지 존재 여부 확인
   * @param memberId 회원 ID
   * @return 존재하면 true, 없으면 false
   */
  boolean existByMemberId(Long memberId);

  /**
   * 닉네임 중복 여부 확인
   * @param nickname 닉네임
   * @return true: 중복 있음, false: 사용 가능
   */
  boolean existByNickname(String nickname);


}
