package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RbbsLike {
  private Long rbbsId;
  private Long memberId;
  private LocalDateTime createDate;
}
