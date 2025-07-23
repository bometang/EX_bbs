package com.KDT.mosi.web.form.board.rbbsReport;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RBbsReport {
  private Long rbbsId;
  private Long memberId;
  private String reason;
  private LocalDateTime reportDate;
}

