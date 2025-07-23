package com.KDT.mosi.domain.member.dao;

import com.KDT.mosi.domain.entity.Role;

import java.util.List;

public interface RoleDAO {

  /**
   * 전체 역할 목록 조회
   * - 시스템에 등록된 모든 역할(Role)을 반환한다.
   *
   * @return 역할 리스트
   */
  List<Role> findAll();

  /**
   * 회원에게 역할을 부여한다.
   * - member_role 테이블에 (memberId, roleId) 쌍을 등록
   *
   * @param memberId 대상 회원 ID
   * @param roleId 부여할 역할 ID (예: "R01", "R02")
   * @return 삽입된 행 수 (정상 1건)
   */
  int addRoleToMember(Long memberId, String roleId);

  /**
   * 회원에게 역할을 부여 (내부 호출용 단축 메서드)
   * - 명확한 메서드명 없이 사용 중이라면 addRoleToMember와 통합하거나 제거 권장
   *
   * @param memberId 회원 ID
   * @param roleId 역할 ID
   */
  void save(long memberId, String roleId);

  /**
   * 특정 회원이 가진 모든 역할 조회
   * - member_role 테이블 기준으로 연결된 역할 정보 반환
   *
   * @param memberId 회원 ID
   * @return 역할 목록 (1명당 여러 역할 가능)
   */
  List<Role> findRolesByMemberId(Long memberId);
}
