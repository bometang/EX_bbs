package com.KDT.mosi.domain.board.bbsReport.svc;

import com.KDT.mosi.domain.board.bbsReport.dao.BbsReportDAO;
import com.KDT.mosi.domain.entity.board.BbsReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BbsReportSVCImpl implements BbsReportSVC {
  private final BbsReportDAO bbsReportDAO;

  @Override
  public String report(BbsReport bbsReport) {
    return bbsReportDAO.report(bbsReport);
  }

  @Override
  public int getTotalCountReport(Long bbsId) {
    return bbsReportDAO.getTotalCountReport(bbsId);
  }

  @Override
  public boolean getReport(Long bbsId, Long memberId) {
    return bbsReportDAO.getReport(bbsId, memberId);
  }
}
