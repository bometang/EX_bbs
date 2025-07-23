package com.KDT.mosi.domain.board.rbbsLike.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;


@Slf4j
@RequiredArgsConstructor
@Repository
public class RBbsLikeDAOImpl implements RBbsLikeDAO {
  final private NamedParameterJdbcTemplate template;

  @Override
  public String toggleLike(Long id, Long rbbsId) {
    // 1. 존재 여부 확인
    String checkSql = "SELECT COUNT(*) FROM rbbs_like WHERE member_id = :id AND rbbs_id = :rbbsId ";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("id",id)
        .addValue("rbbsId",rbbsId);

    int i = template.queryForObject(checkSql, param, Integer.class);


    if (i > 0) {
      // 존재하면 삭제
      String deleteSql = "DELETE FROM rbbs_like WHERE member_id = :id AND rbbs_id = :rbbsId ";
      template.update(deleteSql, param);
      return "DELETED";
    } else {
      // 없으면 삽입
      String insertSql = "INSERT INTO rbbs_like (member_id, rbbs_id) VALUES (:id, :rbbsId)";
      template.update(insertSql, param);
      return "CREATED";
    }
  }

  @Override
  public int getTotalCountLike(Long rbbsId) {
    String sql = "SELECT count(*) FROM rbbs_like WHERE rbbs_id = :rbbsId ";

    SqlParameterSource param = new MapSqlParameterSource().addValue("rbbsId",rbbsId);
    int i = template.queryForObject(sql, param, Integer.class);

    return i;
  }

  @Override
  public boolean getLike(Long rbbsId, Long memberId) {
    String sql = "SELECT count(*) FROM rbbs_like WHERE rbbs_id = :rbbsId AND member_id = :memberId";

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("rbbsId",rbbsId)
        .addValue("memberId",memberId);
    int i = template.queryForObject(sql, param, Integer.class);

    if (i>0) return true;

    return false;
  }
}