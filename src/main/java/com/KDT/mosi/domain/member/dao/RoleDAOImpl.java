package com.KDT.mosi.domain.member.dao;

import com.KDT.mosi.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 역할 DAO 구현 클래스
 * - 역할 조회 및 회원-역할 연결 기능 수행
 */
@Repository
@RequiredArgsConstructor
public class RoleDAOImpl implements RoleDAO {

  private final NamedParameterJdbcTemplate template;

  /**
   * 전체 역할 목록 조회
   * - role 테이블에서 role_id, role_name 조회
   *
   * @return 역할 리스트
   */
  @Override
  public List<Role> findAll() {
    String sql = "SELECT role_id, role_name FROM role ORDER BY role_id";
    return template.query(sql, BeanPropertyRowMapper.newInstance(Role.class));
  }

  /**
   * 회원에게 역할 부여
   * - member_role 테이블에 (member_id, role_id) 삽입
   *
   * @param memberId 회원 ID
   * @param roleId 역할 ID
   * @return 반영된 행 수
   */
  @Override
  public int addRoleToMember(Long memberId, String roleId) {
    String sql = "INSERT INTO member_role (member_id, role_id) VALUES (:memberId, :roleId)";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("roleId", roleId);
    return template.update(sql, param);
  }

  /**
   * 역할 부여 (중복 메서드, 내부 전용 형태)
   * - addRoleToMember와 동일 기능
   * - 향후 리팩토링 시 제거 가능
   *
   * @param memberId 회원 ID
   * @param roleId 역할 ID
   */
  @Override
  public void save(long memberId, String roleId) {
    String sql = "INSERT INTO member_role (member_id, role_id) VALUES (:memberId, :roleId)";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("roleId", roleId);
    template.update(sql, param);
  }

  /**
   * 특정 회원의 역할 목록 조회
   * - member_role 테이블과 role 테이블을 조인하여 조회
   *
   * @param memberId 회원 ID
   * @return Role 리스트
   */
  @Override
  public List<Role> findRolesByMemberId(Long memberId) {
    String sql = """
      SELECT r.role_id, r.role_name
      FROM member_role mr
        JOIN role r ON mr.role_id = r.role_id
      WHERE mr.member_id = :memberId
    """;

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId);

    return template.query(sql, param, BeanPropertyRowMapper.newInstance(Role.class));
  }
}
