package com.KDT.mosi.domain.mypage.seller.svc;

import com.KDT.mosi.domain.entity.SellerPage;
import com.KDT.mosi.domain.mypage.seller.dao.SellerPageDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 판매자 마이페이지 서비스 구현체
 * - DAO를 통해 실제 데이터베이스 연산 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellerPageSVCImpl implements SellerPageSVC {

  private final SellerPageDAO sellerPageDAO;

  /**
   * 회원 ID로 마이페이지 존재 여부 확인
   *
   * @param memberId 회원 ID
   * @return true: 존재함, false: 없음
   */
  @Override
  public boolean existByMemberId(Long memberId) {
    return sellerPageDAO.existByMemberId(memberId);
  }

  /**
   * 판매자 마이페이지 등록
   *
   * @param sellerpage 판매자 페이지 정보
   * @return 생성된 페이지 ID
   */
  @Override
  public Long save(SellerPage sellerpage) {
    return sellerPageDAO.save(sellerpage);
  }

  /**
   * 회원 ID로 판매자 마이페이지 조회
   *
   * @param memberId 회원 ID
   * @return Optional<SellerPage>
   */
  @Override
  public Optional<SellerPage> findByMemberId(Long memberId) {
    return sellerPageDAO.findByMemberId(memberId);
  }

  /**
   * 페이지 ID로 판매자 마이페이지 조회
   *
   * @param pageId 페이지 ID
   * @return Optional<SellerPage>
   */
  @Override
  public Optional<SellerPage> findById(Long pageId) {
    return sellerPageDAO.findById(pageId);
  }

  /**
   * 판매자 마이페이지 수정
   *
   * @param pageId 페이지 ID
   * @param sellerpage 수정할 정보
   * @return 반영된 행 수
   */
  @Override
  public int updateById(Long pageId, SellerPage sellerpage) {
    return sellerPageDAO.updateById(pageId, sellerpage);
  }

  /**
   * 회원 ID로 판매자 마이페이지 삭제
   *
   * @param memberId 회원 ID
   * @return 삭제된 행 수
   */
  @Override
  public int deleteByMemberId(Long memberId) {
    return sellerPageDAO.deleteByMemberId(memberId);
  }

  /**
   * 닉네임 중복 여부 확인
   * - DAO를 통해 닉네임 존재 여부 조회
   *
   * @param nickname 중복 확인할 닉네임
   * @return true: 이미 존재함, false: 사용 가능
   */
  @Override
  public boolean existByNickname(String nickname) {
    return sellerPageDAO.existByNickname(nickname);
  }

}
