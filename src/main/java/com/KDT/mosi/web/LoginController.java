package com.KDT.mosi.web;

import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.member.dao.MemberDAO;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.login.LoginForm;
import com.KDT.mosi.web.form.login.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final MemberDAO memberDAO;

  //로그인화면
  @GetMapping("/login")      // GET http://localhost:9080/login
  public String loginForm(Model model){
    model.addAttribute("loginForm", new LoginForm());
    return "login/loginForm";
  }

  //로그인처리
  @PostMapping("/login")     // POST http://localhost:9080/login
  public String login(
      @Valid @ModelAttribute LoginForm loginForm,
      BindingResult bindingResult,
      HttpServletRequest request
  ) {
    log.info("loginForm={}", loginForm);
    //1) LoginForm객체 바인딩 오류
    if (bindingResult.hasErrors()) {
      return "login/loginForm";
    }

//    //2) 회원존재 유무 체크
//    if(!memberDAO.isExist(loginForm.getEmail())){
//      bindingResult.rejectValue("email", null, "회원정보가 없습니다!");
//      return "login/loginForm";
//    }

    //3) 비밀번호 일치여부 체크
    //3-1) 회원 찾기
    Optional<Member> optionalMember = memberDAO.findByEmail(loginForm.getEmail());
    Member member = optionalMember.get();
    log.info("member={}",member);
    //3-2) 비밀번호 확인
    if(!loginForm.getPasswd().equals(member.getPasswd())){
      bindingResult.rejectValue("passwd", null, "비밀번호가 다릅니다.");
      return "login/loginForm";
    }

    //4) 세션 생셩
    // 세션이 존재하면 해당 세션을 가져오고, 없으면 신규로 생성
    HttpSession session = request.getSession(true);

    //5) 세션에 회원정보 저장
    LoginMember loginMember = new LoginMember(
        member.getMemberId(),
        member.getEmail(),
        member.getNickname()
    );
    session.setAttribute("loginMember", loginMember);

    return "redirect:/";    // 초기화면
  }

  //로그 아웃
  @DeleteMapping("logout")      //DELETE http://localhost:9080/logout
  @ResponseBody
  public ResponseEntity<ApiResponse<String>>  logout(HttpServletRequest request){

    ResponseEntity<ApiResponse<String>> res = null;

    //1) 세션정보 가져오기
    HttpSession session = request.getSession(false);

    //2) 세션 제거
    if (session != null) {
      session.invalidate();
      ApiResponse<String> stringApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, "세션 제거됨!");
      res = ResponseEntity.ok(stringApiResponse);
    }

    return res;
  }
  /**
   * 현재 세션에 저장된 로그인 정보 반환 (AJAX 용)
   * - 로그인 상태: { header.rtcd="S00", body: LoginMember }
   * - 비로그인 상태: { header.rtcd="S00", body: null }
   */
  @GetMapping("/api/auth/user")
  @ResponseBody
  public ResponseEntity<ApiResponse<LoginMember>> currentUser(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    LoginMember loginMember = null;
    if (session != null) {
      loginMember = (LoginMember) session.getAttribute("loginMember");
    }
    // ApiResponse.of(code, body) 를 사용해서 body 에 LoginMember 또는 null 이 담겨 반환됩니다.
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, loginMember));
  }
}

