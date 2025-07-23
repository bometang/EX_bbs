package com.KDT.mosi.web.form.mypage.buyerpage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BuyerPageUpdateForm {

  private Long pageId;

  private Long memberId;

  private MultipartFile imageFile;

  @Size(min = 2, max = 30, message = "닉네임은 2~30자 이내여야 합니다.")
  private String nickname;

  @Pattern(
      regexp = "^$|^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,12}$",
      message = "비밀번호는 8~12자이며, 영문/숫자/특수문자를 포함해야 합니다.")
  private String passwd;

  private String confirmPasswd;

  @Pattern(regexp = "^(010-?\\d{4}-?\\d{4})?$", message = "전화번호 형식은 010-0000-0000입니다.")
  private String tel;

  private String zonecode;
  private String address;
  private String detailAddress;
  private String notification;

  @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
  private String intro;

  @NotBlank(message = "이름은 필수 항목입니다.")
  private String name;

  @Override
  public String toString() {
    return "BuyerPageUpdateForm{" +
        "pageId=" + pageId +
        ", memberId=" + memberId +
        ", nickname='" + nickname + '\'' +
        ", passwd='" + passwd + '\'' +
        ", tel='" + tel + '\'' +
        ", zonecode='" + zonecode + '\'' +
        ", address='" + address + '\'' +
        ", detailAddress='" + detailAddress + '\'' +
        ", notification='" + notification + '\'' +
        ", intro='" + intro + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}

