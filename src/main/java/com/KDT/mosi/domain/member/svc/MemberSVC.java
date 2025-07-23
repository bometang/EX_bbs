package com.KDT.mosi.domain.member.svc;

import com.KDT.mosi.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberSVC {

  /**
   * 회원 등록 (기본형)
   * - 회원 정보만 저장
   *
   * @param member 저장할 회원 정보
   * @return 생성된 회원 ID
   */
  Long join(Member member);

  /**
   * 회원 등록 (확장형)
   * - 회원 정보 저장 + 역할 + 약관 동의 정보 저장
   *
   * @param member 저장할 회원 정보
   * @param roles 부여할 역할 ID 목록 (예: ["R01", "R02"])
   * @param agreedTermsIds 동의한 약관 ID 목록
   * @return 생성된 회원 ID
   */
  Long join(Member member, List<String> roles, List<Long> agreedTermsIds);

  /**
   * 이메일로 회원 조회
   *
   * @param email 회원 이메일
   * @return Optional<Member> 회원 정보
   */
  Optional<Member> findByEmail(String email);

  /**
   * 회원 ID로 회원 조회
   *
   * @param memberId 회원 ID
   * @return Optional<Member> 회원 정보
   */
  Optional<Member> findById(Long memberId);

  /**
   * 이메일 중복 여부 확인
   *
   * @param email 확인할 이메일
   * @return true: 존재함, false: 사용 가능
   */
  boolean isExistEmail(String email);

  /**
   * 이메일 중복 여부 확인 (중복 구현 주의)
   * @param email 확인할 이메일
   * @return true: 존재함, false: 사용 가능
   */
  boolean existsByEmail(String email);

  /**
   * 회원 정보 수정
   * - 이름, 연락처, 닉네임, 주소, 성별, 생년월일 등 수정
   *
   * @param id 수정할 회원 ID
   * @param member 수정 정보
   */
  void modify(Long id, Member member);

  /**
   * 전화번호로 이메일 찾기
   * - 아이디 찾기 기능에서 사용
   *
   * @param tel 전화번호
   * @return 찾은 이메일 (없으면 null)
   */
  String findEmailByTel(String tel);

  /**
   * 이메일 존재 여부 확인
   * - 비밀번호 재설정 요청 시 유효한 이메일인지 검증
   *
   * @param email 입력된 이메일
   * @return 존재하는 이메일 (없으면 null)
   */
  String findEmailByEmail(String email);

  /**
   * 비밀번호 재설정
   * - 이메일 기준으로 비밀번호를 새 값으로 변경
   *
   * @param email 대상 이메일
   * @param newPassword 새 비밀번호 (암호화된 상태)
   * @return true: 성공, false: 실패
   */
  boolean resetPassword(String email, String newPassword);

  /**
   * 회원이 특정 역할을 보유 중인지 확인
   *
   * @param memberId 회원 ID
   * @param roleId 역할 ID (예: "R01")
   * @return true: 해당 역할 보유, false: 없음
   */
  boolean hasRole(Long memberId, String roleId);

  /**
   * 닉네임 중복 여부 확인
   *
   * @param nickname 확인할 닉네임
   * @return true: 존재함, false: 사용 가능
   */
  boolean isExistNickname(String nickname);

  /**
   * 회원 탈퇴
   * - 회원 ID 기준으로 삭제
   *
   * @param memberId 삭제할 회원 ID
   */
  int deleteById(Long memberId);

  Optional<Long> findMemberIdByEmail(String email);

}
