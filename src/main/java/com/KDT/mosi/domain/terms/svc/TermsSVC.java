package com.KDT.mosi.domain.terms.svc;

import com.KDT.mosi.domain.entity.Terms;

import java.util.List;

public interface TermsSVC {

  /**
   * 모든 약관 조회
   * @return 약관 목록
   */
  List<Terms> findAll();
}
