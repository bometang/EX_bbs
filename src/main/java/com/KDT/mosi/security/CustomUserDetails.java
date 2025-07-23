package com.KDT.mosi.security;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security에서 사용하는 사용자 정보 클래스
 * - 회원 기본 정보 + 역할(Role) 정보를 포함
 * - UserDetails 인터페이스 구현
 */
@Getter
public class CustomUserDetails implements UserDetails, Serializable {

  private final Member member;       // 회원 정보
  private final List<Role> roles;    // 보유한 역할 목록

  /**
   * 생성자
   * @param member 회원 객체
   * @param roles  역할 리스트
   */
  public CustomUserDetails(Member member, List<Role> roles) {
    this.member = member;
    this.roles = roles;
  }

  /**
   * 사용자가 보유한 권한 목록 반환
   * 예: "R01" → "ROLE_R01"로 변환하여 반환
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getRoleId())
        .collect(Collectors.toList());
  }

  /** 로그인 비밀번호 반환 */
  @Override
  public String getPassword() {
    return member.getPasswd();
  }

  /** 로그인 ID 반환 (이메일 사용) */
  @Override
  public String getUsername() {
    return member.getEmail();
  }

  /** 계정 만료 여부 */
  @Override public boolean isAccountNonExpired() {
    return true;
  }

  /** 계정 잠금 여부 */
  @Override public boolean isAccountNonLocked() {
    return true;
  }

  /** 비밀번호 만료 여부 */
  @Override public boolean isCredentialsNonExpired() {
    return true;
  }

  /** 계정 활성화 여부 */
  @Override public boolean isEnabled() {
    return true;
  }
}
