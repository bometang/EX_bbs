package com.KDT.mosi.web.api;

import java.util.Arrays;

/**
 *  REST API 응답메세지 헤더에 사용되는 응답코드, 응답메세지 상수화
 */
public enum ApiResponseCode {
  // 성공 응답
  SUCCESS("S00", "Success"),
  // 데이터 없음
  NO_DATA("N04", "No data found"),

  // 공통 예외
  VALIDATION_ERROR("E01", "Validation error occurred"),
  BUSINESS_ERROR("E02", "Business error occurred"),
  ENTITY_NOT_FOUND("E03", "Entity not found"),

  // 사용자 관련 예외
  USER_NOT_FOUND("U01", "User not found"),
  USER_ALREADY_EXISTS("U02", "User already exists"),
  INVALID_PASSWORD("U03", "Invalid password"),

  // 시스템 예외
  INTERNAL_SERVER_ERROR("999","Internal server error");

  private final String rtcd;
  private final String rtmsg;

  ApiResponseCode(String rtcd, String rtmsg) {
    this.rtcd = rtcd;
    this.rtmsg = rtmsg;
  }

  public String getRtcd() {
    return rtcd;
  }

  public String getRtmsg() {
    return rtmsg;
  }

  // 코드로 enum 조회
  public static ApiResponseCode of(String code) {
    return Arrays.stream(values())
        .filter(rc -> rc.getRtcd().equals(code))
        .findFirst()
        .orElse(INTERNAL_SERVER_ERROR);
  }

}
