package com.KDT.mosi.domain.board.rbbsReport.svc;

import com.KDT.mosi.domain.entity.board.RbbsReport;

public interface RBbsReportSVC {
  // 신고 클릭
  String report(RbbsReport rbbsReport);

  // 게시글의 좋아요 갯수
  int getTotalCountReport(Long rbbsId);

  //게시글 신고 유무
  boolean getReport(Long rbbsId,Long memberId);
}
