package com.KDT.mosi.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  // 조건: 대문자, 소문자, 숫자, 특수문자 포함 + 길이 8~12
  private static final String PASSWORD_PATTERN =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,12}$";

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) return false;

    // 기본 정규식 조건 검사
    if (!password.matches(PASSWORD_PATTERN)) return false;

    // 추가 조건: 동일 문자 3번 이상 반복 금지
    if (hasThreeOrMoreRepeatedChars(password)) return false;

    return true;
  }

  // 동일 문자 반복 검사 (예: aaa, 111 등 금지)
  private boolean hasThreeOrMoreRepeatedChars(String password) {
    for (int i = 0; i < password.length() - 2; i++) {
      char c1 = password.charAt(i);
      char c2 = password.charAt(i + 1);
      char c3 = password.charAt(i + 2);
      if (c1 == c2 && c2 == c3) {
        return true;
      }
    }
    return false;
  }
}
