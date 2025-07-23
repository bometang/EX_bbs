package com.KDT.mosi.domain.common.dao;

import com.KDT.mosi.domain.common.CodeId;
import com.KDT.mosi.domain.dto.CodeDTO;

import java.util.List;

public interface CodeDAO {
  List<CodeDTO> loadCodes(CodeId pocdId);;
}
