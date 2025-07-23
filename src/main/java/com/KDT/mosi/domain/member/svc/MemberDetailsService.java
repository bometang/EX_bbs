package com.KDT.mosi.domain.member.svc;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.member.dao.MemberDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

  private final MemberDAO memberDAO;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberDAO.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

    return User.builder()
        .username(member.getEmail())
        .password(member.getPasswd()) // 🔒 암호화된 비밀번호 그대로 전달
        .roles("USER") // 또는 DB에서 역할 받아와서 설정
        .build();
  }
}
