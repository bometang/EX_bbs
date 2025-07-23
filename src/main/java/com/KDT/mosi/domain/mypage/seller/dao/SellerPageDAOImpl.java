package com.KDT.mosi.domain.mypage.seller.dao;

import com.KDT.mosi.domain.entity.SellerPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * 판매자 마이페이지 DAO 구현체
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SellerPageDAOImpl implements SellerPageDAO {

  private final NamedParameterJdbcTemplate template;

  /**
   * 결과셋을 SellerPage 객체로 매핑하는 RowMapper 정의
   */
  private RowMapper<SellerPage> sellerPageRowMapper() {
    return (rs, rowNum) -> {
      SellerPage sellerpage = new SellerPage();
      sellerpage.setPageId(rs.getLong("page_id"));
      sellerpage.setMemberId(rs.getLong("member_id"));
      sellerpage.setNickname(rs.getString("nickname"));
      sellerpage.setImage(rs.getBytes("image"));
      sellerpage.setIntro(rs.getString("intro"));
      sellerpage.setNickname(rs.getString("nickname"));
      sellerpage.setSalesCount(rs.getInt("sales_count"));
      sellerpage.setReviewAvg(rs.getDouble("review_avg"));

      if (rs.getTimestamp("create_date") != null) {
        sellerpage.setCreateDate(rs.getTimestamp("create_date"));
      }
      if (rs.getTimestamp("update_date") != null) {
        sellerpage.setUpdateDate(rs.getTimestamp("update_date"));
      }

      return sellerpage;
    };
  }

  /**
   * 판매자 마이페이지 등록
   * - page_id는 시퀀스로 자동 생성
   */
  @Override
  public Long save(SellerPage sellerpage) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO SELLER_PAGE ");
    sql.append("(PAGE_ID, MEMBER_ID, IMAGE, INTRO, NICKNAME, SALES_COUNT, REVIEW_AVG, CREATE_DATE, UPDATE_DATE) ");
    sql.append("VALUES (SELLER_PAGE_SEQ.NEXTVAL, :memberId, :image, :intro, :nickname, :salesCount, :reviewAvg, systimestamp, systimestamp) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(sellerpage);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    template.update(sql.toString(), param, keyHolder, new String[]{"page_id"});
    return ((Number) keyHolder.getKeys().get("page_id")).longValue();
  }

  /**
   * 회원 ID로 마이페이지 조회
   * - 존재하지 않으면 Optional.empty 반환
   */
  @Override
  public Optional<SellerPage> findByMemberId(Long memberId) {
    String sql = "SELECT * FROM SELLER_PAGE WHERE MEMBER_ID = :memberId";
    SqlParameterSource param = new MapSqlParameterSource("memberId", memberId);

    try {
      SellerPage sellerpage = template.queryForObject(sql, param, sellerPageRowMapper());
      return Optional.of(sellerpage);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * 페이지 ID로 마이페이지 조회
   */
  @Override
  public Optional<SellerPage> findById(Long pageId) {
    String sql = "SELECT * FROM SELLER_PAGE WHERE PAGE_ID = :pageId";
    SqlParameterSource param = new MapSqlParameterSource("pageId", pageId);

    try {
      SellerPage sellerpage = template.queryForObject(
          sql, param, BeanPropertyRowMapper.newInstance(SellerPage.class)
      );
      return Optional.of(sellerpage);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * 회원 ID로 마이페이지 존재 여부 확인
   * - COUNT(*) > 0이면 true
   */
  @Override
  public boolean existByMemberId(Long memberId) {
    String sql = "SELECT COUNT(*) FROM seller_page WHERE member_id = :memberId";
    SqlParameterSource param = new MapSqlParameterSource("memberId", memberId);
    Integer count = template.queryForObject(sql, param, Integer.class);
    return count != null && count > 0;
  }

  /**
   * 닉네임 중복 여부 확인
   *
   * @param nickname 확인할 닉네임
   * @return true: 이미 존재하는 닉네임, false: 사용 가능한 닉네임
   */
  @Override
  public boolean existByNickname(String nickname) {
    // ✅ SQL: 닉네임으로 중복 여부 확인
    String sql = "SELECT COUNT(*) FROM seller_page WHERE nickname = :nickname";

    // ✅ 파라미터 바인딩용 Map (NamedParameter 방식)
    Map<String, Object> param = Map.of("nickname", nickname);

    // ✅ 쿼리 실행 → 결과는 Integer 타입으로 반환
    Integer count = template.queryForObject(sql, param, Integer.class);

    // ✅ 결과 해석: 1개 이상이면 true (중복), 아니면 false
    return count != null && count > 0;
  }


  /**
   * 마이페이지 정보 수정
   * - pageId 기준으로 image, intro, salesCount, reviewAvg, updateDate 갱신
   */
  @Override
  public int updateById(Long pageId, SellerPage sellerpage) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE SELLER_PAGE ");
    sql.append("SET IMAGE = :image, ");
    sql.append("    INTRO = :intro, ");
    sql.append("    NICKNAME = :nickname, ");
    sql.append("    SALES_COUNT = :salesCount, ");
    sql.append("    REVIEW_AVG = :reviewAvg, ");
    sql.append("    UPDATE_DATE = systimestamp ");
    sql.append("WHERE PAGE_ID = :pageId");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("image", sellerpage.getImage())
        .addValue("intro", sellerpage.getIntro())
        .addValue("nickname", sellerpage.getNickname())
        .addValue("salesCount", sellerpage.getSalesCount())
        .addValue("reviewAvg", sellerpage.getReviewAvg())
        .addValue("pageId", pageId);

    return template.update(sql.toString(), param);
  }

  /**
   * 회원 ID로 마이페이지 삭제
   */
  @Override
  public int deleteByMemberId(Long memberId) {
    String sql = "DELETE FROM SELLER_PAGE WHERE MEMBER_ID = :memberId";
    SqlParameterSource param = new MapSqlParameterSource("memberId", memberId);
    return template.update(sql, param);
  }
}
