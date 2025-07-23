package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bbs {
  private Long bbsId;
  private String bcategory;
  private String status;
  private String title;
  private Long memberId;
  private String nickname;
  private byte[] pic;
  private Long hit;
  private String bcontent;
  private Long pbbsId;
  private Long bgroup;
  private Long step;
  private Long bindent;
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
  private int commentCnt;
}
