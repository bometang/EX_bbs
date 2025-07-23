package com.KDT.mosi.domain.terms.dao;

import com.KDT.mosi.domain.entity.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 약관 DAO 구현체
 * - 약관 목록 조회 및 회원의 약관 동의 등록 기능 담당
 */
@Repository
@RequiredArgsConstructor
public class TermsDAOImpl implements TermsDAO {

  private final NamedParameterJdbcTemplate template;

  /**
   * 모든 약관 조회
   * - terms 테이블의 전체 약관 정보를 ID 순으로 반환
   *
   * @return 약관 리스트
   */
  @Override
  public List<Terms> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT terms_id, name, content, is_required, version, created_at ");
    sql.append("  FROM terms ");
    sql.append(" ORDER BY terms_id ");

    return template.query(sql.toString(), BeanPropertyRowMapper.newInstance(Terms.class));
  }

  /**
   * 회원의 약관 동의 저장
   * - member_terms 테이블에 (member_id, terms_id, 동의 시각) 삽입
   *
   * @param memberId 회원 ID
   * @param termsId 약관 ID
   * @return 반영된 행 수
   */
  @Override
  public int agreeTerms(Long memberId, Long termsId) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO member_terms ");
    sql.append("      (member_id, terms_id, agreed_at) ");
    sql.append("VALUES (:memberId, :termsId, systimestamp) ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("termsId", termsId);

    return template.update(sql.toString(), param);
  }

  /**
   * 약관 동의 저장 (중복 메서드)
   * - agreeTerms와 동일 기능 수행
   * - 구조상 별도 정의된 경우, 리팩토링 시 통합 가능
   *
   * @param memberId 회원 ID
   * @param termsId 약관 ID
   */
  @Override
  public void save(long memberId, long termsId) {
    String sql = "INSERT INTO member_terms (member_id, terms_id, agreed_at) " +
        "VALUES (:memberId, :termsId, systimestamp)";

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("termsId", termsId);

    template.update(sql, param);
  }
}
