package com.KDT.mosi.domain.board.bbsLike.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;


@Slf4j
@RequiredArgsConstructor
@Repository
public class BbsLikeDAOImpl implements BbsLikeDAO {
  final private NamedParameterJdbcTemplate template;

  @Override
  public String toggleLike(Long id, Long bbsId) {
    // 1. 존재 여부 확인
    String checkSql = "SELECT COUNT(*) FROM bbs_like WHERE member_id = :id AND bbs_id = :bbsId ";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("id",id)
        .addValue("bbsId",bbsId);

    int i = template.queryForObject(checkSql, param, Integer.class);


    if (i > 0) {
      // 존재하면 삭제
      String deleteSql = "DELETE FROM bbs_like WHERE member_id = :id AND bbs_id = :bbsId ";
      template.update(deleteSql, param);
      return "DELETED";
    } else {
      // 없으면 삽입
      String insertSql = "INSERT INTO bbs_like (member_id, bbs_id) VALUES (:id, :bbsId)";
      template.update(insertSql, param);
      return "CREATED";
    }
  }

  @Override
  public int getTotalCountLike(Long bbsId) {
    String sql = "SELECT count(*) FROM bbs_like WHERE bbs_id = :bbsId ";

    SqlParameterSource param = new MapSqlParameterSource().addValue("bbsId",bbsId);
    int i = template.queryForObject(sql, param, Integer.class);

    return i;
  }

  @Override
  public boolean getLike(Long bbsId, Long memberId) {
    String sql = "SELECT count(bbs_id) FROM bbs_like WHERE bbs_id=:bbsId AND member_id=:memberId ";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("bbsId",bbsId)
        .addValue("memberId",memberId);
    int i = template.queryForObject(sql, param, Integer.class);
    if(i>0) return true;
    return false;
  }
}