package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.bbsReport.svc.BbsReportSVC;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.board.BbsReport;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.board.bbsReport.Report;
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
@RequestMapping("/api/bbs/{bbsId}/report")
@RequiredArgsConstructor
public class ApiBbsReportController {

  private final BbsReportSVC bbsReportSVC;

  /**
   * 게시글 신고
   * POST /api/bbs/{bbsId}/report
   * Body: { "reason": "신고 사유" }
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> report(
      @PathVariable("bbsId") Long bbsId,
      @RequestBody Report reportForm,
      HttpSession session
  ) {

    Member loginMember = (Member) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    // PathVariable·세션 정보 채워주기
    reportForm.setBbsId(bbsId);
    reportForm.setMemberId(loginMember.getMemberId());

    if (reportForm.getReason() == null || reportForm.getReason().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "신고 사유를 입력하세요.");
    }

    BbsReport report = new BbsReport();
    BeanUtils.copyProperties(reportForm, report);

    // 서비스에는 엔티티로 변환하거나, SVC가 Report DTO를 받도록
    bbsReportSVC.report(report);

    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, null));
  }
}
