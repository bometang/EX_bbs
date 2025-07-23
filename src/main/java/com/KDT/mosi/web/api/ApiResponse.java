package com.KDT.mosi.web.api;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * REST API 응답메세지 표준 구현
 *
 */
@Slf4j
@Getter
@ToString
public class ApiResponse<T> {
  private final Header header;    //응답헤더
  private final T body;           //응답바디
  private final Paging paging;    //페이지정보

  //페이지 정보가 미포함된 표준 응답메세지 생성
  private ApiResponse(Header header, T body) {
    this.header = header;
    this.body = body;
    this.paging = null;
  }

  //페이지 정보가 포함된 표준 응답메세지 생성
  private ApiResponse(Header header, T body, Paging paging) {
    this.header = header;
    this.body = body;
    this.paging = paging;
  }

  // 1. 기본 헤더 (details가 없는 경우)
  @Getter
  @ToString
  private static class Header {
    private final String rtcd;      //응답코드
    private final String rtmsg;     //응답메시지
    private final Map<String, String> details;  //상세 메세지

    Header(String rtcd, String rtmsg, Map<String, String> details) {
      this.rtcd = rtcd;
      this.rtmsg = rtmsg;
      this.details = details;
    }
  }

  @Getter
  @ToString
  public static class Paging {
    private int numOfRows;    //레코드건수
    private int pageNo;       //요청페이지
    private int totalCount;   //총건수

    public Paging(int pageNo, int numOfRows,  int totalCount) {
      this.pageNo = pageNo;
      this.numOfRows = numOfRows;
      this.totalCount = totalCount;
    }
  }

  // API 응답 생성 메소드-상세 오류 미포함
  public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T body) {
    return new ApiResponse<>(new Header(responseCode.getRtcd(), responseCode.getRtmsg(), null), body);
  }

  public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T body, Paging paging) {
    return new ApiResponse<>(new Header(responseCode.getRtcd(), responseCode.getRtmsg(), null), body, paging);
  }

  // API 응답 생성 메소드-상세 오류 포함
  public static <T> ApiResponse<T> withDetails(ApiResponseCode responseCode, Map<String, String> details, T body) {
    return new ApiResponse<>(new Header(responseCode.getRtcd(), responseCode.getRtmsg(), details), body);
  }

  public static <T> ApiResponse<T> withDetails(ApiResponseCode responseCode, Map<String, String> details, T body, Paging paging) {
    return new ApiResponse<>(new Header(responseCode.getRtcd(), responseCode.getRtmsg(), details), body, paging);
  }
}
