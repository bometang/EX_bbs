package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BbsLike {
  private Long bbsId;
  private Long memberId;
  private LocalDateTime createDate;
}
