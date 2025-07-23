package com.KDT.mosi.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)  // 👉 검증기 연결
@Target({ElementType.FIELD})  // 👉 필드에만 사용
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
  String message() default "비밀번호는 8~12자이며, 대문자/소문자/숫자/특수문자를 포함해야 하고 동일 문자 3회 이상 반복은 불가합니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
