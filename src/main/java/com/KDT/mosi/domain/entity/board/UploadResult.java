package com.KDT.mosi.domain.entity.board;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadResult {
  private Long uploadId;
  private String url;
  private Long uploadGroup;
  private String originalName;
  private long size;
}
