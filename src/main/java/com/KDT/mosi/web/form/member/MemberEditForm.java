package com.KDT.mosi.web.form.member;

import com.KDT.mosi.web.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 회원 정보 수정 폼
 * - 회원 정보 수정을 위한 입력 필드를 담은 폼 객체
 */
@Data
public class MemberEditForm {

  /** 이메일 (읽기 전용) */
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  /** 이름 (최대 50자) */
  @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
  private String name;


  /** 새 비밀번호 (영문 대소문자, 숫자, 특수문자 포함, 동일 문자 3회 이상 반복 불가) */
  @ValidPassword(message = "비밀번호는 8~12자이며, 영문 대소문자, 숫자, 특수문자를 포함해야 하며 동일 문자 3회 이상 반복은 불가합니다.")
  private String passwd;

  /** 비밀번호 확인 */
  private String confirmPasswd;

  /** 전화번호 (정규식 검사) */
  @Pattern(regexp = "^(010-\\d{4}-\\d{4})?$", message = "전화번호 형식은 010-0000-0000입니다.")
  private String tel;

  /** 닉네임 (2~30자) */
  @Size(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
  private String nickname;

  /** 성별 ('남자' 또는 '여자') */
  @Pattern(regexp = "^(남자|여자)?$", message = "성별은 '남자' 또는 '여자'만 가능합니다.")
  private String gender;

  /** 우편번호 */
  private String zonecode;

  /** 기본 주소 */
  private String address;

  /** 상세 주소 */
  private String detailAddress;

  /** 생년월일 (yyyy-MM-dd 형식 문자열) */
  private String birthDate;

  /** 프로필 이미지 파일 */
  private MultipartFile picFile;
}
