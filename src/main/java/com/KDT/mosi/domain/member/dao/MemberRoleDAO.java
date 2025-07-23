package com.KDT.mosi.domain.member.dao;

import com.KDT.mosi.domain.entity.Role;

import java.util.List;

public interface MemberRoleDAO {

  /**
   * 회원에게 역할 부여
   * @param memberId 회원 ID
   * @param roleId 역할 ID (예: "R01", "R02")
   * @return 반영된 행 수
   */
  int addRole(Long memberId, String roleId);

  /**
   * 회원이 보유한 역할 조회
   * @param memberId 회원 ID
   * @return 역할 목록
   */
  List<Role> findRolesByMemberId(Long memberId);

  /**
   * 회원이 특정 역할을 보유하고 있는지 확인
   * @param memberId 회원 ID
   * @param roleId 역할 ID
   * @return true: 보유함, false: 없음
   */
  boolean hasRole(Long memberId, String roleId);

  /**
   * 회원의 모든 역할 제거 (회원 탈퇴 등에서 사용)
   * @param memberId 회원 ID
   * @return 삭제된 행 수
   */
  int deleteByMemberId(Long memberId);


}
