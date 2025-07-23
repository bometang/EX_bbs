package com.KDT.mosi.web.form.board.bbsUpload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class saveUploadApi {
  private Long imageId;
  private Long bbsId;          // FK
  private int  sortOrder;
  private String filePath;     // 접근 URL 또는 저장 경로
  private String originalName;
  private String savedName;
  private List<MultipartFile> files;
}
