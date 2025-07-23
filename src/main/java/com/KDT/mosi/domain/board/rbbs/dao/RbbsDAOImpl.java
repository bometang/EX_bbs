package com.KDT.mosi.domain.board.rbbs.dao;

import com.KDT.mosi.domain.entity.board.Rbbs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class RbbsDAOImpl implements RbbsDAO {
  private final NamedParameterJdbcTemplate template;

  @Override
  public Long save(Rbbs rbbs) {
    boolean parentsRbbs = rbbs.getPrbbsId() != null;

    if (parentsRbbs) {
      // 부모 댓글 조회
      Rbbs parent = this.findById(rbbs.getPrbbsId())
          .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다. id=" + rbbs.getPrbbsId()));

      // 새 답글 위치 계산 & shift
      int newStep = this.updateStep(parent.getBgroup(), parent);

      // 계층 정보 세팅
      rbbs.setBgroup(parent.getBgroup());
      rbbs.setStep((long)newStep);
      rbbs.setBindent(parent.getBindent() + 1);
    }

    // SQL 조립
    StringBuffer sql = new StringBuffer();
    if (!parentsRbbs) {
      sql.append("INSERT INTO rbbs (rbbs_id, bbs_id, status, prbbs_id, bcontent, member_id, bgroup, step, bindent) ")
          .append("VALUES (rbbs_rbbs_id_seq.nextval, :bbsId, 'R0201', NULL, :bcontent, :memberId, rbbs_rbbs_id_seq.CURRVAL, 0, 0)");
    } else {
      sql.append("INSERT INTO rbbs (rbbs_id, bbs_id, status, prbbs_id, bcontent, member_id, bgroup, step, bindent) ")
          .append("VALUES (rbbs_rbbs_id_seq.nextval, :bbsId, 'R0201', :prbbsId, :bcontent, :memberId, :bgroup, :step, :bindent)");
    }

    SqlParameterSource param = new BeanPropertySqlParameterSource(rbbs);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql.toString(), param, keyHolder, new String[]{"rbbs_id"});

    return ((Number)keyHolder.getKeys().get("rbbs_id")).longValue();
  }

  @Override
  public List<Rbbs> findAll(Long bbsId) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT r.rbbs_id as rbbs_id, r.bbs_id as bbs_id, r.status as status, r.prbbs_id as prbbs_id, r.member_id as member_id, ")
        .append("m.nickname as nickname, ")
        .append("m.pic as pic, ")
        .append("CASE ")
        .append("WHEN r.status = 'R0202' THEN to_clob('삭제된 게시글입니다.') ")
        .append("ELSE r.bcontent ")
        .append("END AS bcontent, ")
        .append("r.bgroup as bgroup, r.step as step, r.bindent as bindent, r.create_date as create_date, r.update_date as update_date ")
        .append("FROM rbbs r ")
        .append("LEFT JOIN member m ON r.member_id = m.member_id ")
        .append("WHERE r.bbs_id = :bbsId ")
        .append("  AND r.status <> 'R0203' ")
        .append("ORDER BY r.bgroup DESC, r.step ASC, r.rbbs_id ASC");

    Map<String, Long> params = Map.of("bbsId", bbsId);
    return template.query(sql.toString(), params, BeanPropertyRowMapper.newInstance(Rbbs.class));
  }

  @Override
  public List<Rbbs> findAll(Long bbsId, int pageNo, int numOfRows) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT r.rbbs_id as rbbs_id, r.bbs_id as bbs_id, r.status as status, r.prbbs_id as prbbs_id, r.member_id as member_id, ")
        .append("m.nickname as nickname, ")
        .append("m.pic as pic, ")
        .append("CASE ")
        .append("WHEN r.status = 'R0202' THEN to_clob('삭제된 게시글입니다.') ")
        .append("ELSE r.bcontent ")
        .append("END AS bcontent, ")
        .append("r.bgroup as bgroup, r.step as step, r.bindent as bindent, r.create_date as create_date, r.update_date as update_date ")
        .append("FROM rbbs r ")
        .append("LEFT JOIN member m ON r.member_id = m.member_id ")
        .append("WHERE r.bbs_id = :bbsId ")
        .append("  AND r.status <> 'R0203' ")
        .append("ORDER BY r.bgroup DESC, r.step ASC, r.rbbs_id ASC ")
        .append("OFFSET (:pageNo - 1) * :numOfRows ROWS ")
        .append("FETCH NEXT :numOfRows ROWS ONLY");

    Map<String, Object> params = Map.of(
        "bbsId", bbsId,
        "pageNo", pageNo,
        "numOfRows", numOfRows
    );
    return template.query(sql.toString(), params, BeanPropertyRowMapper.newInstance(Rbbs.class));
  }

  @Override
  public int getTotalCount(Long bbsId) {
    String sql = "SELECT COUNT(rbbs_id) FROM rbbs WHERE bbs_id = :bbsId AND status <> 'R0203' ";
    Map<String, Long> params = Map.of("bbsId", bbsId);
    return template.queryForObject(sql, params, Integer.class);
  }

  @Override
  public Optional<Rbbs> findById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT r.rbbs_id as rbbs_id, r.bbs_id as bbs_id, r.status as status, r.prbbs_id as prbbs_id,r.bindent as bindent,  r.member_id as member_id, ")
        .append("m.nickname as nickname, ")
        .append("CASE ")
        .append("WHEN r.status = 'R0202' THEN to_clob('삭제된 게시글입니다.') ")
        .append("ELSE r.bcontent ")
        .append("END AS bcontent, ")
        .append("r.bgroup as bgroup, r.step as step,  r.create_date as create_date, r.update_date as update_date ")
        .append("FROM rbbs r ")
        .append("LEFT JOIN member m ON r.member_id = m.member_id ")
        .append("WHERE r.rbbs_id = :id");

    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
    try {
      Rbbs rbbs = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Rbbs.class));
      return Optional.of(rbbs);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public int deleteById(Long id) {
    String sql = "UPDATE rbbs SET status = 'R0202', update_date = SYSTIMESTAMP WHERE rbbs_id = :id";
    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
    return template.update(sql, param);
  }

  @Override
  public int updateById(Long rbbsId, Rbbs rbbs) {
    String sql = "UPDATE rbbs SET bcontent = :bcontent, update_date = SYSTIMESTAMP WHERE rbbs_id = :rbbsId";
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("bcontent", rbbs.getBcontent())
        .addValue("rbbsId", rbbsId);
    return template.update(sql, param);
  }

  @Override
  public int updateStep(Long bgroup, Rbbs parentRbbs) {
    // 1) 같은 계층 내 마지막 step 조회
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT NVL(MAX(step), :parentStep) ")
        .append("FROM rbbs ")
        .append("WHERE bgroup = :bgroup ")
        .append("  AND prbbs_id = :parentId ")
        .append("  AND bindent = :childBindent");

    long childBindent = parentRbbs.getBindent() + 1;
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("bgroup", bgroup)
        .addValue("parentId", parentRbbs.getRbbsId())
        .addValue("parentStep", parentRbbs.getStep())
        .addValue("childBindent", childBindent);

    Long lastStep = template.queryForObject(sql.toString(), params, Long.class);
    int newStep = lastStep.intValue() + 1;

    // 2) 기존 댓글들 shift
    String shiftSql = "UPDATE rbbs SET step = step + 1 WHERE bgroup = :bgroup AND step >= :newStep";
    params.addValue("newStep", newStep);
    template.update(shiftSql, params);

    return newStep;
  }
}

