package com.KDT.mosi.web.form.member;

import com.KDT.mosi.web.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 회원가입 폼
 * - 가입 시 사용되는 필드 정보를 담은 폼 객체
 */
@Data
public class MemberJoinForm {

  /** 이메일 (필수, 중복 확인 대상) */
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  /** 이름 (필수) */
  @NotBlank(message = "이름은 필수 입력값입니다.")
  private String name;

  /** 비밀번호 (필수, 8~12자) */
  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 8, max = 12, message = "비밀번호는 8자 이상 12자 이내여야 합니다.")

  @ValidPassword(message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 모두 포함해야 하며 동일 문자를 3회 이상 반복할 수 없습니다.")
  private String passwd;

  /** 비밀번호 확인 */
  private String confirmPasswd;

  /** 전화번호 (정규식 검사) */
  @Pattern(regexp = "^(010-\\d{4}-\\d{4})?$", message = "전화번호 형식은 010-0000-0000입니다.")
  private String tel;

  /** 닉네임 (2~30자, 중복 확인 대상) */
  @Size(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
  private String nickname;

  /** 성별 ('남자' 또는 '여자') */
  @Pattern(regexp = "^(남자|여자)?$", message = "성별은 '남자' 또는 '여자'만 가능합니다.")
  private String gender;

  /** 기본 주소 */
  private String address;

  /** 생년월일 (yyyy-MM-dd 형식 문자열) */
  private String birthDate;

  /** 프로필 이미지 파일 */
  private MultipartFile picFile;

  /** 선택한 역할 ID 목록 (예: R01, R02) */
  private List<String> roles;

  /** 동의한 약관 ID 목록 */
  private List<Long> agreedTermsIds;

  /** 우편번호 */
  private String zonecode;

  /** 상세 주소 */
  private String detailAddress;
}
