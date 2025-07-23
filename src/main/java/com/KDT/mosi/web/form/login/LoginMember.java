package com.KDT.mosi.web.form.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LoginMember {
  private Long memberId;   //내부 관리용 멤버아이디
  private String email;    //회원 로그인 아이디
  private String nickname; //별칭
}

