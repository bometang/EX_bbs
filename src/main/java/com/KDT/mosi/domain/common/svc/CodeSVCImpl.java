package com.KDT.mosi.domain.common.svc;

import com.KDT.mosi.domain.common.CodeId;
import com.KDT.mosi.domain.common.dao.CodeDAO;
import com.KDT.mosi.domain.dto.CodeDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeSVCImpl implements CodeSVC{

  private final CodeDAO codeDAO;
  private List<CodeDTO> b01;
  private Map<CodeId, List<CodeDTO>> codeMap;

  @Override
  public List<CodeDTO> getCodes(CodeId pcodeId) {
    return codeDAO.loadCodes(pcodeId);
  }

  @PostConstruct  // 생성자 호출후 실행될 메소드에 선언하면 해당 메소드가 자동 호출
  private List<CodeDTO> getM01Code(){
    log.info("getB01Code() 수행됨!");
    b01 = codeDAO.loadCodes(CodeId.B01);
    return b01;
  }

  public List<CodeDTO> getB01() {
    return b01;
  }
}
