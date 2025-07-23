package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.rbbsReport.svc.RBbsReportSVC;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.board.RbbsReport;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.board.rbbsReport.RBbsReport;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/bbs/comments/{rbbsId}/report")
@RequiredArgsConstructor
public class ApiRBbsReportController {

  private final RBbsReportSVC rbbsReportSVC;


  @PostMapping
  public ResponseEntity<ApiResponse<Void>> report(
      @PathVariable("rbbsId") Long rbbsId,
      @RequestBody RBbsReport reportForm,
      HttpSession session
  ) {
    Member loginMember = (Member) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    // PathVariable·세션 정보 채워주기
    reportForm.setRbbsId(rbbsId);
    reportForm.setMemberId(loginMember.getMemberId());

    if (reportForm.getReason() == null || reportForm.getReason().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 사유를 입력하세요.");
    }

    RbbsReport report = new RbbsReport();
    BeanUtils.copyProperties(reportForm, report);

    // 서비스에는 엔티티로 변환하거나, SVC가 Report DTO를 받도록
    rbbsReportSVC.report(report);

    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, null));
  }
}
