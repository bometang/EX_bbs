package com.KDT.mosi.domain.common.svc;

import com.KDT.mosi.domain.common.CodeId;
import com.KDT.mosi.domain.dto.CodeDTO;

import java.util.List;

public interface CodeSVC {
  /**
   * 코드정보 가져오기
   * @param pcodeId  부모코드
   * @return 하위코드
   */
  List<CodeDTO> getCodes(CodeId pcodeId);

  /**
   * M01 코드 정보 가져오기
   * @return M01 코드 정보
   */
  List<CodeDTO> getB01();

}
