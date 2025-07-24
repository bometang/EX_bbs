package com.KDT.mosi.domain.board.bbs.dao;

import com.KDT.mosi.domain.entity.board.Bbs;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Repository
public class BbsDAOImpl implements BbsDAO {
  final private NamedParameterJdbcTemplate template;

  @Override
  public Long save(Bbs bbs) {
    boolean parentsBbs = bbs.getPbbsId() != null;
    String statusBbs = bbs.getStatus();

    if (parentsBbs) {
      // 1) 부모 조회
      Bbs parent = this.findById(bbs.getPbbsId())
          .orElseThrow(() -> new IllegalArgumentException("부모 게시글이 없습니다. id=" + bbs.getPbbsId()));

      // 2) step 계산 및 기존 답글 shift
      // 게시 상태라면 기존 답글 shift
      if ("B0201".equals(statusBbs)) {
        int newStep = this.updateStep(parent.getBgroup(), parent);
        bbs.setStep((long)newStep);
      } else {
        // 임시저장일 땐 shift 없이, 부모 기준으로만 +1
        bbs.setStep(parent.getStep() + 1);
      }
      // 3) 계층 정보 세팅
      bbs.setBcategory(parent.getBcategory());
      bbs.setBgroup(parent.getBgroup());
      bbs.setBindent(parent.getBindent() + 1);
    }
    // else { /* 아무것도 안 해도 됩니다 */ }

    // — SQL 조립 (원래 쓰시던 구조 그대로) —
    StringBuffer sql = new StringBuffer();
    if ("B0201".equals(statusBbs)) {
      if (!parentsBbs) {
        sql.append("INSERT INTO bbs (bbs_id, bcategory, status, title, member_id, bcontent, pbbs_id, bgroup, step, bindent) ")
            .append("VALUES (bbs_bbs_id_seq.nextval, :bcategory, 'B0201', :title, :memberId, :bcontent, NULL, bbs_bbs_id_seq.CURRVAL, 0, 0)");
      } else {
        sql.append("INSERT INTO bbs (bbs_id, bcategory, status, title, member_id, bcontent, pbbs_id, bgroup, step, bindent) ")
            .append("VALUES (bbs_bbs_id_seq.nextval, :bcategory, 'B0201', :title, :memberId, :bcontent, :pbbsId, :bgroup, :step, :bindent)");
      }
    }
    else if ("B0203".equals(statusBbs)) {
      if (!parentsBbs) {
        sql.append("INSERT INTO bbs (bbs_id, bcategory, status, title, member_id, bcontent, pbbs_id, bgroup, step, bindent) ")
            .append("VALUES (bbs_bbs_id_seq.nextval, :bcategory, 'B0203', :title, :memberId, :bcontent, NULL, bbs_bbs_id_seq.CURRVAL, 0, 0)");
      } else {
        sql.append("INSERT INTO bbs (bbs_id, bcategory, status, title, member_id, bcontent, pbbs_id, bgroup, step, bindent) ")
            .append("VALUES (bbs_bbs_id_seq.nextval, :bcategory, 'B0203', :title, :memberId, :bcontent, :pbbsId, :bgroup, :step, :bindent)");
      }
    }

    // 파라미터 바인딩 & 실행
    SqlParameterSource param = new BeanPropertySqlParameterSource(bbs);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql.toString(), param, keyHolder, new String[]{"bbs_id"});

    Number key = (Number)keyHolder.getKeys().get("bbs_id");
    return key.longValue();
  }


  /**
   * 게시글 전체 목록
   * @return 게시글 전체 목록
   */
  @Override
  public List<Bbs> findAll() {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.' ");
    sql.append("ELSE b.title ");
    sql.append("END AS title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date, ");
    sql.append("b.bindent as bindent ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("where b.status <> 'B0203' ");
    sql.append("ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC ");

    Map<String, Object> params = Collections.emptyMap();

    //db요청
    List<Bbs> list = template.query(sql.toString(),params, BeanPropertyRowMapper.newInstance(Bbs.class));

    return list;
  }

  /**
   * 전체 목록의 페이지
   * @param pageNo  페이지 번호
   * @param numOfRows 한페이지당 게시글 수
   * @return 게시글
   */
  @Override
  public List<Bbs> findAll(int pageNo, int numOfRows) {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("b.status as status, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.' ");
    sql.append("ELSE b.title ");
    sql.append("END AS title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("b.hit AS hit, ");
    sql.append("b.bindent AS bindent, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("where b.status <> 'B0203' ");
    sql.append("ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC ");
    sql.append("  OFFSET (:pageNo -1) * :numOfRows ROWS ");
    sql.append("FETCH NEXT :numOfRows ROWS ONLY ");


    Map<String, Integer> map = Map.of("pageNo", pageNo, "numOfRows", numOfRows);
    List<Bbs> list = template.query(sql.toString(), map, BeanPropertyRowMapper.newInstance(Bbs.class));

    if (!list.isEmpty()) {
      List<Long> bbsIds = list.stream().map(Bbs::getBbsId).toList();

      StringBuffer countSql = new StringBuffer();
      countSql.append("SELECT bbs_id, COUNT(*) AS cnt ");
      countSql.append("FROM rbbs ");
      countSql.append("WHERE bbs_id IN (:bbsIds) ");
      countSql.append("AND status <> 'C0202' ");
      countSql.append("GROUP BY bbs_id ");

      Map<String,Object> cntParams = Map.of("bbsIds", bbsIds);

      List<Map<String,Object>> cntRows = template.queryForList(countSql.toString(), cntParams);

      Map<Long,Integer> cntMap = cntRows.stream()
          .collect(Collectors.toMap(
              r -> ((Number)r.get("bbs_id")).longValue(),
              r -> ((Number)r.get("cnt")).intValue()
          ));
      list.forEach(b -> b.setCommentCnt(cntMap.getOrDefault(b.getBbsId(), 0)));
    }
    return list;
  }

  /**
   * 전체 게시글 갯수
   * @return
   */
  @Override
  public int getTotalCount() {
    String sql = "SELECT count(bbs_id) FROM bbs where status <> 'B0203' ";

    SqlParameterSource param = new MapSqlParameterSource();
    int i = template.queryForObject(sql, param, Integer.class);

    return i;
  }

  /**
   * 특정 카테고리 게시글 전체 목록
   * @param bcategory
   * @return
   */
  @Override
  public List<Bbs> findAll(String bcategory) {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.' ");
    sql.append("ELSE b.title ");
    sql.append("END AS title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("b.hit AS hit, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date, ");
    sql.append("b.bindent as bindent ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("  WHERE b.bcategory = :bcategory ");
    sql.append("  AND b.status <> 'B0203' ");
    sql.append("ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("bcategory", bcategory);
    //db요청
    List<Bbs> list = template.query(sql.toString(), param, BeanPropertyRowMapper.newInstance(Bbs.class));

    return list;
  }

  /**
   * 특정 카테고리 게시글 전체 목록의 페이지
   * @param bcategory
   * @param pageNo
   * @param numOfRows
   * @return
   */
  @Override
  public List<Bbs> findAll(String bcategory, int pageNo, int numOfRows) {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.' ");
    sql.append("ELSE b.title ");
    sql.append("END AS title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("b.hit AS hit, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date, ");
    sql.append("b.bindent as bindent ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("WHERE b.bcategory = :bcategory ");
    sql.append("  AND b.status <> 'B0203' ");
    sql.append("ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC ");
    sql.append("  OFFSET (:pageNo -1) * :numOfRows ROWS ");
    sql.append("FETCH NEXT :numOfRows ROWS only ");

    Map<String, Object> map = Map.of("pageNo", pageNo, "numOfRows", numOfRows, "bcategory",bcategory);
    List<Bbs> list = template.query(sql.toString(), map, BeanPropertyRowMapper.newInstance(Bbs.class));

    return list;
  }

  /**
   * 특정 카테고리 게시글 전체 게시글 갯수
   * @param bcategory
   * @return
   */
  @Override
  public int getTotalCount(String bcategory) {
    String sql = "SELECT count(bbs_id) FROM bbs where bcategory = :bcategory  AND status <> 'B0203' ";

    Map<String, Object> map = Map.of("bcategory",bcategory);
    int i = template.queryForObject(sql, map, Integer.class);

    return i;
  }

  /**
   * 게시글 조회
   * @param id 게시글 번호
   * @return  게시글 정보
   */
  @Override
  public Optional<Bbs> findById(Long id) {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("b.status as status, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.' ");
    sql.append("ELSE b.title ");
    sql.append("END AS title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("m.pic as pic, ");
    sql.append("b.hit AS hit, ");
    sql.append("CASE ");
    sql.append("WHEN b.status = 'B0202' THEN to_clob('삭제된 게시글입니다.') ");
    sql.append("ELSE b.bcontent ");
    sql.append("END AS bcontent, ");
    sql.append("b.pbbs_id AS pbbs_id, ");
    sql.append("b.bgroup AS bgroup, ");
    sql.append("b.step AS step, ");
    sql.append("b.bindent AS bindent, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("where b.bbs_id = :id ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("id",id);

    Bbs bbs = null;
    try {
      bbs = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Bbs.class));
    } catch (EmptyResultDataAccessException e) { //template.queryForObject() : 레코드를 못찾으면 예외 발생
      return Optional.empty();
    }

    return Optional.of(bbs);
  }

  /**
   * 게시글 삭제시 코드만 업데이트
   * @param id
   * @return
   */
  @Override
  public int deleteById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE bbs ");
    sql.append("   SET status = 'B0202',update_date = systimestamp ");
    sql.append(" WHERE bbs_id = :id ");

    //수동매핑
    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

    int rows = template.update(sql.toString(), param); // 수정된 행의 수 반환

    return rows;
  }

  /**
   * 게시글 여러개 삭제시 코드만 업데이트
   * @param ids
   * @return
   */
  @Override
  public int deleteByIds(List<Long> ids) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE bbs ");
    sql.append("   SET status = 'B0202',update_date = systimestamp ");
    sql.append(" WHERE bbs_id IN ( :ids) ");

    //수동매핑
    SqlParameterSource param = new MapSqlParameterSource().addValue("ids",ids);

    int rows = template.update(sql.toString(), param); // 수정된 행의 수 반환

    return rows;
  }

  /**
   * 게시글의 제목,내용을 수정
   * @param bbsId
   * @param bbs
   * @return
   */
  @Override
  public int updateById(Long bbsId, Bbs bbs) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE bbs ");
    sql.append("   SET title = :title, bcontent = :bcontent, update_date = systimestamp ");
    sql.append(" WHERE bbs_id = :bbsId ");

    //수동매핑
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("title", bbs.getTitle())
        .addValue("bcontent", bbs.getBcontent())
        .addValue("bbsId", bbsId);

    int rows = template.update(sql.toString(), param); // 수정된 행의 수 반환

    return rows;
  }

  /**
   * 답글,대답글 추가시 목록 순서 변경
   * @param bgroup
   * @param parentBbs
   * @return
   */
  @Override
  public int updateStep(Long bgroup, Bbs parentBbs) {
    long pIndent  = parentBbs.getBindent();

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT NVL(MAX(step), :parentStep) ");
    sql.append("FROM bbs ");
    sql.append("WHERE bgroup       = :bgroup ");


    // — bindent 레벨별 분기 —
    if (pIndent == 0) {              // 댓글 → 대댓글들

    } else if (pIndent == 1) {       // 대댓글 → 대대댓글들
      sql.append("  AND pbbs_id = :parentId ");
    } else {                         // 대대댓글 → 더 깊이 없음
      // MAX(step)이 parentStep 그대로이므로 아래서 +1만 하면 됨
    }

    MapSqlParameterSource param = new MapSqlParameterSource()
        .addValue("bgroup",       bgroup)
        .addValue("parentId",     parentBbs.getBbsId())
        .addValue("parentStep",   parentBbs.getStep());

    Long lastStep = template.queryForObject(sql.toString(), param, Long.class);
    int newStep = lastStep.intValue() + 1;

    // 2) 계산된 위치(newStep) 이상인 모든 글의 step +1
    StringBuffer shiftSql = new StringBuffer();
    shiftSql.append("UPDATE bbs ");
    shiftSql.append("   SET step = step + 1 ");
    shiftSql.append(" WHERE bgroup = :bgroup ");
    shiftSql.append("   AND step   >= :newStep");

    // 같은 param에 newStep 값만 추가
    param.addValue("newStep", newStep);
    template.update(shiftSql.toString(), param);

    // 3) 새 답글의 step 리턴
    return newStep;
  }
  /**
   * 게시글 조회수 증가
   * @param id 게시글 번호
   * @return 수정된 행의 수
   */
  @Override
  public int increaseHit(Long id) {
    String sql = "UPDATE bbs SET hit = hit + 1 WHERE bbs_id = :id";
    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
    return template.update(sql, param);
  }

  /**
   * 최근 10건 안에 같은 제목·내용이 존재하는지
   * @param title    새 글 제목
   * @param bcontent 새 글 내용
   * @return true  : 중복 있음
   *         false : 중복 없음
   */
  @Override
  public boolean existsDuplicateRecent(String title, String bcontent) {

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT COUNT(*)                                        ");
    sql.append("  FROM ( SELECT title,                                     ");
    sql.append("                 DBMS_LOB.SUBSTR(bcontent, 4000, 1) AS bcontent_sub ");
    sql.append("           FROM bbs                                   ");
    sql.append("          WHERE status <> 'B0203'                     ");
    sql.append("          ORDER BY create_date DESC                   ");
    sql.append("          FETCH FIRST 10 ROWS ONLY )                  ");
    sql.append(" WHERE title   = :title                               ");
    sql.append("   OR bcontent_sub = :bcontent                      ");

    Map<String, Object> param = Map.of(
        "title",    title,
        "bcontent", bcontent
    );

    Integer cnt = template.queryForObject(sql.toString(), param, Integer.class);
    return cnt != null && cnt > 0;
  }

  @Override
  public Optional<Bbs> findTemporaryStorageById(Long memberId, Long pbbsId) {
    //sql
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT ");
    sql.append("b.bbs_id as bbs_id, ");
    sql.append("b.bcategory as bcategory, ");
    sql.append("b.status as status, ");
    sql.append("b.title as title, ");
    sql.append("NVL(m.member_id, 0) AS member_id, ");
    sql.append("m.nickname as nickname, ");
    sql.append("b.hit AS hit, ");
    sql.append("b.bcontent as bcontent, ");
    sql.append("b.pbbs_id AS pbbs_id, ");
    sql.append("b.bgroup AS bgroup, ");
    sql.append("b.step AS step, ");
    sql.append("b.bindent AS bindent, ");
    sql.append("b.create_date AS create_date, ");
    sql.append("b.update_date as update_date ");
    sql.append("FROM bbs b ");
    sql.append("LEFT JOIN member m ");
    sql.append("ON b.member_id = m.member_id ");
    sql.append("where b.member_id = :memberId ");
    sql.append("and b.status = 'B0203' ");
    sql.append("and NVL(b.pbbs_id, 0) = NVL(:pbbsId, 0) ");
    sql.append("ORDER BY b.update_date DESC ");
    sql.append("FETCH FIRST 1 ROWS ONLY ");

    Long safePbbsId = (pbbsId != null) ? pbbsId : 0L;

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId",memberId)
        .addValue("pbbsId",   safePbbsId);

    Bbs bbs = null;
    try {
      bbs = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Bbs.class));
    } catch (EmptyResultDataAccessException e) { //template.queryForObject() : 레코드를 못찾으면 예외 발생
      return Optional.empty();
    }

    return Optional.of(bbs);
  }

  @Override
  public int deleteTemporaryStorage(Long memberId, Long pbbsId) {
    Long safePbbsId = (pbbsId != null) ? pbbsId : 0L;
    StringBuffer sql = new StringBuffer();
    sql.append("DELETE FROM bbs ");
    sql.append(" WHERE member_id = :memberId ");
    sql.append("   AND status    = 'B0203' ");
    sql.append("   AND NVL(pbbs_id, 0) = NVL(:pbbsId, 0)");

    MapSqlParameterSource param = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("pbbsId",   safePbbsId);

    return template.update(sql.toString(), param);
  }

}
