package com.KDT.mosi.security;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.Role;
import com.KDT.mosi.domain.member.dao.MemberRoleDAO;
import com.KDT.mosi.domain.member.svc.MemberSVC;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사용자 인증 정보를 제공하는 서비스 클래스
 * - Spring Security에서 사용자 정보를 조회할 때 사용
 */
@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberSVC memberSVC;           // 회원 정보 서비스
  private final MemberRoleDAO memberRoleDAO;   // 회원 역할 DAO

  /**
   * 이메일로 사용자 정보 조회
   * @param email 로그인 시 입력한 이메일
   * @return UserDetails 구현체(CustomUserDetails)
   * @throws UsernameNotFoundException 이메일로 사용자를 찾지 못한 경우
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // 1. 회원 정보 조회
    Member member = memberSVC.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + email));

    // 2. 역할 정보 조회
    List<Role> roles = memberRoleDAO.findRolesByMemberId(member.getMemberId());

    // 3. UserDetails 반환
    return new CustomUserDetails(member, roles);
  }
}
