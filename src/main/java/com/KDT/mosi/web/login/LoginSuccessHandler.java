package com.KDT.mosi.web.login;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.member.svc.MemberSVC;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberSVC memberSVC;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    String email = authentication.getName();
    Member member = memberSVC.findByEmail(email).orElseThrow();

    // ✅ 세션에 로그인 정보 저장
    HttpSession session = request.getSession(true);
    session.setAttribute("loginMember", member);
    session.setAttribute("loginMemberId", member.getMemberId());  // 🔧 추가

    log.info("✅ 로그인 성공: 세션에 loginMember 저장 - {}", member.getEmail());

    response.sendRedirect("/");
  }

}
