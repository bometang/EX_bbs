package com.KDT.mosi.domain.member.dao;

import com.KDT.mosi.domain.entity.Member;
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

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberDAOImpl implements MemberDAO {

  private final NamedParameterJdbcTemplate template;

  /**
   * 회원 저장
   * - 시퀀스를 사용하여 member_id 자동 생성
   * - 생성된 ID를 KeyHolder로 반환
   */
  @Override
  public Long save(Member member) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date, pic, create_date, update_date) ");
    sql.append("VALUES (member_member_id_seq.nextval, :email, :name, :passwd, :tel, :nickname, :gender, :address, :birthDate, :pic, systimestamp, systimestamp)");

    SqlParameterSource param = new BeanPropertySqlParameterSource(member);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    template.update(sql.toString(), param, keyHolder, new String[]{"member_id"});

    return ((Number) keyHolder.getKeys().get("member_id")).longValue();
  }

  /**
   * 이메일로 회원 조회
   * - 존재하지 않으면 Optional.empty() 반환
   */
  @Override
  public Optional<Member> findByEmail(String email) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT * FROM member ");
    sql.append(" WHERE email = :email");

    try {
      Member member = template.queryForObject(
          sql.toString(),
          new MapSqlParameterSource("email", email),
          BeanPropertyRowMapper.newInstance(Member.class)
      );
      return Optional.of(member);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * 회원 ID로 조회
   * - 존재하지 않으면 Optional.empty() 반환
   */
  @Override
  public Optional<Member> findById(Long memberId) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT * FROM member ");
    sql.append(" WHERE member_id = :memberId");

    try {
      Member member = template.queryForObject(
          sql.toString(),
          new MapSqlParameterSource("memberId", memberId),
          BeanPropertyRowMapper.newInstance(Member.class)
      );
      return Optional.of(member);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * 이메일 중복 여부 확인
   * @return true: 이미 존재함, false: 사용 가능
   */
  @Override
  public boolean isExistEmail(String email) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT count(*) FROM member ");
    sql.append(" WHERE email = :email");

    Integer count = template.queryForObject(
        sql.toString(),
        new MapSqlParameterSource("email", email),
        Integer.class
    );
    return count != null && count > 0;
  }

  /**
   * 회원 정보 수정
   * - name, passwd, tel, nickname, gender, address, birth_date, pic, update_date
   * - 수정된 행 수 반환
   */
  @Override
  public int update(Member member) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE member SET ");
    sql.append("  name = :name, ");
    sql.append("  passwd = :passwd, ");
    sql.append("  tel = :tel, ");
    sql.append("  nickname = :nickname, ");
    sql.append("  gender = :gender, ");
    sql.append("  address = :address, ");
    sql.append("  birth_date = :birthDate, ");
    sql.append("  pic = :pic, ");
    sql.append("  update_date = systimestamp ");
    sql.append("WHERE member_id = :memberId");

    SqlParameterSource param = new BeanPropertySqlParameterSource(member);
    return template.update(sql.toString(), param);
  }

  /**
   * 전화번호로 이메일 찾기
   * - 존재하지 않으면 Optional.empty() 반환
   */
  @Override
  public Optional<String> findEmailByTel(String tel) {
    String sql = "SELECT email FROM member WHERE tel = :tel";

    try {
      String email = template.queryForObject(
          sql,
          new MapSqlParameterSource("tel", tel),
          String.class
      );
      return Optional.ofNullable(email);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  /**
   * 비밀번호 재설정
   * - email 기준으로 passwd와 update_date 수정
   * @return 수정된 행 수
   */
  @Override
  public int updatePassword(String email, String newPassword) {
    String sql = "UPDATE member SET passwd = :passwd, update_date = systimestamp WHERE email = :email";

    MapSqlParameterSource param = new MapSqlParameterSource()
        .addValue("passwd", newPassword)
        .addValue("email", email);

    return template.update(sql, param);
  }

  /**
   * 닉네임 중복 여부 확인
   * @return true: 중복 있음, false: 사용 가능
   */
  @Override
  public boolean isExistNickname(String nickname) {
    String sql = "SELECT COUNT(*) FROM member WHERE nickname = :nickname";

    Integer count = template.queryForObject(
        sql,
        new MapSqlParameterSource("nickname", nickname),
        Integer.class
    );

    return count != null && count > 0;
  }

  /**
   * 회원 탈퇴
   * - member_id를 기준으로 회원 정보를 삭제한다.
   *
   * @param memberId 삭제할 회원의 고유 ID
   * @return 삭제된 행 수 (1: 성공, 0: 실패)
   */
  @Override
  public int deleteById(Long memberId) {
    // 삭제할 SQL 정의
    String sql = "DELETE FROM member WHERE member_id = :memberId";

    // 파라미터 바인딩
    MapSqlParameterSource param = new MapSqlParameterSource("memberId", memberId);

    // SQL 실행 후 삭제된 행 수 반환
    return template.update(sql, param);
  }

  @Override
  public Optional<Long> findMemberIdByEmail(String email) {
    String sql = "SELECT member_id FROM member WHERE email = :email";
    Map<String, Object> params = Map.of("email", email);

    try {
      Long memberId = template.queryForObject(sql, params, Long.class);
      return Optional.ofNullable(memberId);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean isExistMemberId(Long memberId) {
    return findById(memberId).isPresent();
  }

  @Override
  public int updatePic(Long memberId, byte[] pic) {
    String sql = ""
        + "UPDATE member "
        + "   SET pic = :pic "
        + " WHERE member_id = :memberId";

    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("pic", pic)
        .addValue("memberId", memberId);

    return template.update(sql, params);
  }
}
