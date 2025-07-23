package com.KDT.mosi.web.form.login;

import lombok.Data;

@Data
//public class LoginForm {
//  @NotBlank(message = "이메일은 필수!")
//  @Email(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$",message = "이메일 형식에 맞지 않습니다.")
//  @Size(min=7,max=50,message = "이메일 크기는 7자~50자 사이 여야합니다.")
//  private String email;
//  @NotBlank(message = "비밀번호는 필수!")
//  @Size(min=4,max=15,message = "이메일 크기는 4자~15자 사이 여야합니다.")
//  @Pattern(
//      regexp = "^[A-Za-z0-9!@#$%^&*]+$",
//      message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)만 사용할 수 있습니다."
//  )
//  private String passwd;
//}

public class LoginForm {
  private String email;
  private String passwd;
}