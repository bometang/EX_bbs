package com.KDT.mosi.domain.member.svc;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.member.dao.MemberDAO;
import com.KDT.mosi.domain.member.dao.MemberRoleDAO;
import com.KDT.mosi.domain.mypage.buyer.dao.BuyerPageDAO;
import com.KDT.mosi.domain.terms.dao.TermsDAO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 회원 서비스 구현체
 * - 회원 등록, 조회, 수정, 역할 부여, 약관 동의 처리 등
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSVCImpl implements MemberSVC {

  private final MemberDAO memberDAO;
  private final MemberRoleDAO memberroleDAO;
  private final TermsDAO termsDAO;
  private final BuyerPageDAO buyerPageDAO;

  /**
   * 회원 등록 (기본형)
   * - 역할/약관 동의 없이 회원 정보만 저장
   *
   * @param member 등록할 회원 정보
   * @return 등록된 회원의 ID
   */
  @Override
  @Transactional
  public Long join(Member member) {
    // 1. 회원 정보 저장
    Long memberId = memberDAO.save(member);

    // 2. 기본 역할(R01) 자동 부여
    memberroleDAO.addRole(memberId, "R01");

    return memberId;
  }

  /**
   * 회원 등록 (확장형)
   * - 회원 정보 + 역할 + 약관 동의 정보 함께 저장
   *
   * @param member 회원 정보
   * @param roles 선택한 역할 ID 목록 (예: ["R01", "R02"])
   * @param agreedTermsIds 동의한 약관 ID 목록
   * @return 등록된 회원의 ID
   */
  @Override
  @Transactional
  public Long join(Member member, List<String> roles, List<Long> agreedTermsIds) {
    Long memberId = memberDAO.save(member);
    log.info("회원 저장 완료: {}", memberId);

    // 역할 등록
    if (roles != null && !roles.isEmpty()) {
      log.info("역할 등록 시작: {}", roles);
      for (String roleId : roles) {
        memberroleDAO.addRole(memberId, roleId);
      }
      log.info("역할 등록 완료");
    } else {
      log.info("역할 정보 없음 또는 비어 있음");
    }

    // 약관 동의 등록
    if (agreedTermsIds != null && !agreedTermsIds.isEmpty()) {
      log.info("약관 동의 등록 시작: {}", agreedTermsIds);
      for (Long termsId : agreedTermsIds) {
        termsDAO.agreeTerms(memberId, termsId);
      }
      log.info("약관 동의 등록 완료");
    } else {
      log.info("약관 동의 정보 없음 또는 비어 있음");
    }

    return memberId;
  }

  /**
   * 이메일로 회원 조회
   *
   * @param email 조회할 이메일
   * @return Optional<Member> 회원 정보
   */
  @Override
  public Optional<Member> findByEmail(String email) {
    return memberDAO.findByEmail(email);
  }

  /**
   * 회원 ID로 회원 조회
   *
   * @param memberId 회원 ID
   * @return Optional<Member> 회원 정보
   */
  @Override
  public Optional<Member> findById(Long memberId) {
    return memberDAO.findById(memberId);
  }

  /**
   * 이메일 중복 여부 확인 (회원가입 시 사용)
   *
   * @param email 확인할 이메일
   * @return true: 존재함, false: 사용 가능
   */
  @Override
  public boolean isExistEmail(String email) {
    return memberDAO.isExistEmail(email);
  }

  /**
   * 이메일 존재 여부 확인 (비밀번호 재설정 등에서 사용)
   *
   * @param email 확인할 이메일
   * @return true: 존재함, false: 없음
   */
  @Override
  public boolean existsByEmail(String email) {
    return memberDAO.findByEmail(email).isPresent();
  }

  /**
   * 회원 정보 수정
   * - 이름, 전화번호, 닉네임, 주소 등 전체 필드 갱신
   *
   * @param id 수정할 회원 ID
   * @param member 수정할 정보
   */
  @Override
  public void modify(Long id, Member member) {
    member.setMemberId(id);

    // 기존 비밀번호 유지 처리
    if (member.getPasswd() == null || member.getPasswd().isBlank()) {
      memberDAO.findById(id).ifPresent(existing -> member.setPasswd(existing.getPasswd()));
    }

    memberDAO.update(member);
  }

  /**
   * 전화번호로 이메일 찾기
   *
   * @param tel 전화번호
   * @return 이메일 (없으면 null)
   */
  @Override
  public String findEmailByTel(String tel) {
    return memberDAO.findEmailByTel(tel).orElse(null);
  }

  /**
   * 이메일로 이메일 확인 (존재 여부만 체크할 때 사용)
   *
   * @param email 이메일
   * @return 해당 이메일 또는 null
   */
  @Override
  public String findEmailByEmail(String email) {
    return memberDAO.findByEmail(email)
        .map(Member::getEmail)
        .orElse(null);
  }

  /**
   * 비밀번호 재설정
   *
   * @param email 대상 이메일
   * @param newPassword 새 비밀번호 (암호화된 상태)
   * @return true: 성공, false: 실패
   */
  @Override
  public boolean resetPassword(String email, String newPassword) {
    return memberDAO.updatePassword(email, newPassword) == 1;
  }

  /**
   * 회원이 특정 역할을 가지고 있는지 확인
   *
   * @param memberId 회원 ID
   * @param roleId 확인할 역할 ID
   * @return true: 해당 역할 있음, false: 없음
   */
  @Override
  public boolean hasRole(Long memberId, String roleId) {
    return memberroleDAO.findRolesByMemberId(memberId)
        .stream()
        .anyMatch(role -> role.getRoleId().equals(roleId));
  }

  /**
   * 닉네임 중복 여부 확인
   *
   * @param nickname 확인할 닉네임
   * @return true: 존재함, false: 사용 가능
   */
  @Override
  public boolean isExistNickname(String nickname) {
    return memberDAO.isExistNickname(nickname);
  }

  @Override
  public int deleteById(Long memberId) {
    buyerPageDAO.deleteByMemberId(memberId);
    return memberDAO.deleteById(memberId);
  }

  @Override
  public Optional<Long> findMemberIdByEmail(String email) {
    return memberDAO.findMemberIdByEmail(email);
  }

}
