package com.KDT.mosi.web.exception;

import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/**
 * 전역 예외 처리 컨트롤러
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 403 접근 거부 예외 처리
   */
  @ExceptionHandler(AccessDeniedException.class)
  public String handleAccessDeniedException(AccessDeniedException e,
                                            HttpServletRequest request,
                                            Model model) {
    log.warn("403 예외 발생: {}", e.getMessage());

    model.addAttribute("path", request.getRequestURI());
    model.addAttribute("errorMessage", e.getMessage());
    return "error/403";
  }

  // 필요 시 다른 예외도 여기에 추가 가능

  /** 파일 크기 제한 초과 시 (413 Payload Too Large) */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseBody
  public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
    log.warn("파일 업로드 크기 초과: {}", ex.getMessage());
    // HTTP 200으로 내려서 클라이언트 성공 콜백에서 rtcd만 검사하게
    return ResponseEntity.ok(
        ApiResponse.of(ApiResponseCode.FILE_TOO_LARGE, null)
    );
  }

  /** Multipart 처리 중 오류 발생 시 (400 Bad Request) */
  @ExceptionHandler(MultipartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiResponse<Void> handleMultipartError(MultipartException ex) {
    log.warn("Multipart 오류: {}", ex.getMessage());
    return ApiResponse.of(ApiResponseCode.INVALID_PARAMETER, null);
  }
}
