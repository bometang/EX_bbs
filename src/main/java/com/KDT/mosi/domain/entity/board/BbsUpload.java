package com.KDT.mosi.domain.entity.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BbsUpload {
  private Long uploadId;
  private Long bbsId;          // FK
  private Long uploadGroup;
  private String fileType;
  private int  sortOrder;
  private String filePath;     // 접근 URL 또는 저장 경로
  private String originalName;
  private String savedName;
  private LocalDateTime uploadedAt;
}
