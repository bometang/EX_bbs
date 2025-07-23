package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Rbbs {
  private Long rbbsId;
  private Long bbsId;
  private String status;
  private Long memberId;
  private String nickname;
  private String bcontent;
  private Long prbbsId;
  private Long bgroup;
  private Long step;
  private Long bindent;
  private byte[] pic;
  private String picData;
  private boolean liked;    // 내가 좋아요 눌렀는지
  private boolean reported;
  private int likeCount;    // 총 좋아요 수
  private LocalDateTime createDate;
  private LocalDateTime updateDate;
}
