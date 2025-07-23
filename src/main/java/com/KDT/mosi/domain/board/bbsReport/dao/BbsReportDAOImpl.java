package com.KDT.mosi.domain.board.bbsReport.dao;

import com.KDT.mosi.domain.entity.board.BbsReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;


@Slf4j
@RequiredArgsConstructor
@Repository
public class BbsReportDAOImpl implements BbsReportDAO {
  final private NamedParameterJdbcTemplate template;

  @Override
  public String report(BbsReport bbsReport) {
    // 1. 존재 여부 확인
    String checkSql = "SELECT COUNT(*) FROM bbs_report WHERE member_id = :memberId AND bbs_id = :bbsId ";
    SqlParameterSource param = new BeanPropertySqlParameterSource(bbsReport);

    int i = template.queryForObject(checkSql, param, Integer.class);


    if (i > 0) {
      // 존재할 경우

      return "ALREADY_REPORTED";
    } else {
      // 없으면 삽입
      String insertSql = "INSERT INTO bbs_report (member_id, bbs_id,reason) VALUES (:memberId, :bbsId, :reason)";
      template.update(insertSql, param);
      return "CREATED";
    }
  }

  @Override
  public int getTotalCountReport(Long bbsId) {
    String sql = "SELECT count(*) FROM bbs_report WHERE bbs_id = :bbsId ";

    SqlParameterSource param = new MapSqlParameterSource().addValue("bbsId",bbsId);
    int i = template.queryForObject(sql, param, Integer.class);

    return i;
  }

  @Override
  public boolean getReport(Long bbsId, Long memberId) {
    String sql = "SELECT count(bbs_id) FROM bbs_report WHERE bbs_id=:bbsId AND member_id=:memberId ";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("bbsId",bbsId)
        .addValue("memberId",memberId);
    int i = template.queryForObject(sql, param, Integer.class);
    if(i>0) return true;
    return false;
  }
}
