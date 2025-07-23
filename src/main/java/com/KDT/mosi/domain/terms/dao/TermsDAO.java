package com.KDT.mosi.domain.terms.dao;

import com.KDT.mosi.domain.entity.Terms;

import java.util.List;

public interface TermsDAO {

  /**
   * 모든 약관 조회
   * - terms 테이블의 전체 약관 목록 반환
   *
   * @return 약관 목록
   */
  List<Terms> findAll();

  /**
   * 회원이 약관에 동의
   * - member_terms 테이블에 동의 기록 저장
   *
   * @param memberId 회원 ID
   * @param termsId 약관 ID
   * @return 삽입된 행 수
   */
  int agreeTerms(Long memberId, Long termsId);

  /**
   * 회원이 약관에 동의 (별도 명시 메서드)
   * - agreeTerms와 동일한 기능 (호환성 또는 구조상 분리용)
   *
   * @param memberId 회원 ID
   * @param termsId 약관 ID
   */
  void save(long memberId, long termsId);
}
