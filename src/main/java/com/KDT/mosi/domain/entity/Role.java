package com.KDT.mosi.domain.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Role implements Serializable {
  private String roleId;     // R01, R02
  private String roleName;   // 구매자, 판매자
}
