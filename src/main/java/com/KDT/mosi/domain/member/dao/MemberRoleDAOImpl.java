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
 * 회원 역할 관리 DAO 구현체
 * - MEMBER_ROLE 테이블 전용
 */
@Repository
@RequiredArgsConstructor
public class MemberRoleDAOImpl implements MemberRoleDAO {

  private final NamedParameterJdbcTemplate template;

  /**
   * 회원에게 역할 부여
   */
  @Override
  public int addRole(Long memberId, String roleId) {
    String sql = "INSERT INTO member_role (member_id, role_id) VALUES (:memberId, :roleId)";

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("roleId", roleId);

    return template.update(sql, param);
  }

  /**
   * 특정 회원의 역할 조회
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

  @Override
  public boolean hasRole(Long memberId, String roleId) {
    String sql = """
    SELECT COUNT(*)
    FROM member_role
    WHERE member_id = :memberId AND role_id = :roleId
  """;

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("roleId", roleId);

    Integer count = template.queryForObject(sql, param, Integer.class);
    return count != null && count > 0;
  }

  @Override
  public int deleteByMemberId(Long memberId) {
    String sql = "DELETE FROM member_role WHERE member_id = :memberId";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId);
    return template.update(sql, param);
  }





}
