package com.KDT.mosi.web.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BusinessValidationException extends RuntimeException {
  private final Map<String, String> details;

  public BusinessValidationException(String message) {
    super(message);
    this.details = new HashMap<>();
  }

  public BusinessValidationException(String message, Map<String, String> details) {
    super(message);
    this.details = details;
  }

  // 글로벌 에러를 추가하는 편의 메서드
  public void addGlobalError(String message) {
    details.put("global", message);
  }

  // 필드 에러를 추가하는 편의 메서드
  public void addFieldError(String field, String message) {
    details.put(field, message);
  }
}
