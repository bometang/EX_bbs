package com.KDT.mosi.web.form.board.bbsReport;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Report {
  private Long bbsId;
  private Long memberId;
  private String reason;
  private LocalDateTime reportDate;
}

