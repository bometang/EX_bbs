package com.KDT.mosi.web;


import com.KDT.mosi.domain.member.svc.MemberSVC;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.login.LoginMember;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequestMapping("/api/member")
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class ApiMemberController {

  private final MemberSVC memberSVC;

  @GetMapping("/me")
//  @ResponseBody
  public ResponseEntity<ApiResponse<Long>> getMemberId(
      HttpSession session
  ) {
    // 1) 로그인 정보 꺼내기
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    // 2) memberId만 추출해서 반환
    Long memberId = loginMember.getMemberId();


    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, memberId));
  }


}
