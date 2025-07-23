package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RbbsReport {
  private Long rbbsId;
  private Long memberId;
  private String reason;
  private LocalDateTime reportDate;
}
