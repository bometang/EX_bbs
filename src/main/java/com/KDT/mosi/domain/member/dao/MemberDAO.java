package com.KDT.mosi.domain.member.dao;

import com.KDT.mosi.domain.entity.Member;

import java.util.Optional;

public interface MemberDAO {

  /**
   * 회원 등록
   * - 회원 정보를 데이터베이스에 저장하고, 생성된 member_id를 반환한다.
   *
   * @param member 저장할 회원 정보
   * @return 생성된 회원 번호 (memberId)
   */
  Long save(Member member);

  /**
   * 이메일로 회원 조회
   * - 이메일을 기준으로 회원 정보를 조회한다.
   * - 결과가 없을 경우 Optional.empty() 반환
   *
   * @param email 조회할 회원 이메일
   * @return Optional<Member> 회원 정보
   */
  Optional<Member> findByEmail(String email);

  /**
   * 회원 ID로 회원 조회
   * - memberId로 회원 정보를 조회한다.
   * - 결과가 없을 경우 Optional.empty() 반환
   *
   * @param memberId 회원 고유 번호
   * @return Optional<Member> 회원 정보
   */
  Optional<Member> findById(Long memberId);

  /**
   * 이메일 중복 확인
   * - 주어진 이메일이 이미 등록되어 있는지 확인한다.
   *
   * @param email 중복 확인할 이메일
   * @return true: 존재함, false: 사용 가능
   */
  boolean isExistEmail(String email);

  /**
   * 회원 정보 수정
   * - 회원의 기본 정보(name, passwd, tel 등)를 수정한다.
   *
   * @param member 수정할 회원 정보
   * @return 수정된 행(row)의 수
   */
  int update(Member member);

  /**
   * 전화번호로 이메일 찾기
   * - 사용자가 비밀번호를 잊었을 때, 전화번호로 이메일을 찾아준다.
   *
   * @param tel 전화번호
   * @return Optional<String> 이메일
   */
  Optional<String> findEmailByTel(String tel);

  /**
   * 비밀번호 재설정
   * - 주어진 이메일을 기준으로 비밀번호를 새 값으로 업데이트한다.
   *
   * @param email 대상 회원 이메일
   * @param newPassword 새 비밀번호 (암호화된 상태)
   * @return 수정된 행(row)의 수
   */
  int updatePassword(String email, String newPassword);

  /**
   * 닉네임 중복 여부 확인
   * - 주어진 닉네임이 이미 등록되어 있는지 확인한다.
   *
   * @param nickname 중복 확인할 닉네임
   * @return true: 중복 있음, false: 사용 가능
   */
  boolean isExistNickname(String nickname);

  /**
   * 회원 탈퇴
   * - MEMBER_ID 기준으로 회원 삭제
   *
   * @param memberId 삭제할 회원 ID
   * @return 삭제된 행 수
   */
  int deleteById(Long memberId);

  Optional<Long> findMemberIdByEmail(String email);

  /**
   * 회원 ID 존재 여부 확인
   *
   * @param memberId 확인할 회원 ID
   * @return true: 존재함, false: 없음
   */
  boolean isExistMemberId(Long memberId);

  int updatePic(Long memberId, byte[] pic);
}
