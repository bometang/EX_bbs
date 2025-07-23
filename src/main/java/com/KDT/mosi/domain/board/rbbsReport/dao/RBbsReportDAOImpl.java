package com.KDT.mosi.domain.board.rbbsReport.dao;

import com.KDT.mosi.domain.entity.board.RbbsReport;
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
public class RBbsReportDAOImpl implements RBbsReportDAO {
  final private NamedParameterJdbcTemplate template;

  @Override
  public String report(RbbsReport rbbsReport) {
    // 1. 존재 여부 확인
    String checkSql = "SELECT COUNT(*) FROM rbbs_report WHERE member_id = :memberId AND rbbs_id = :rbbsId ";
    SqlParameterSource param = new BeanPropertySqlParameterSource(rbbsReport);

    int i = template.queryForObject(checkSql, param, Integer.class);


    if (i > 0) {
      // 존재할 경우

      return "ALREADY_REPORTED";
    } else {
      // 없으면 삽입
      String insertSql = "INSERT INTO rbbs_report (member_id, rbbs_id,reason) VALUES (:memberId, :rbbsId, :reason) ";
      template.update(insertSql, param);
      return "CREATED";
    }
  }

  @Override
  public int getTotalCountReport(Long rbbsId) {
    String sql = "SELECT count(*) FROM rbbs_report WHERE rbbs_id = :rbbsId ";

    SqlParameterSource param = new MapSqlParameterSource().addValue("rbbsId",rbbsId);
    int i = template.queryForObject(sql, param, Integer.class);

    return i;
  }

  @Override
  public boolean getReport(Long rbbsId, Long memberId) {
    String sql = "SELECT count(rbbs_id) FROM rbbs_report WHERE rbbs_id=:rbbsId AND member_id=:memberId ";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("rbbsId",rbbsId)
        .addValue("memberId",memberId);
    int i = template.queryForObject(sql, param, Integer.class);
    if(i>0) return true;
    return false;
  }
}
