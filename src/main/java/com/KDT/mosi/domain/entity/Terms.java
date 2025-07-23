package com.KDT.mosi.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Terms {
  private Long termsId;
  private String name;
  private String content;
  private String isRequired; // 'Y' or 'N'
  private String version;
  private Timestamp createdAt;
}
