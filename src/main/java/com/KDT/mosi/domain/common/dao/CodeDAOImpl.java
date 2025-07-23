package com.KDT.mosi.domain.common.dao;

import com.KDT.mosi.domain.common.CodeId;
import com.KDT.mosi.domain.dto.CodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CodeDAOImpl implements CodeDAO {

  private final NamedParameterJdbcTemplate template;

  @Override
  public List<CodeDTO> loadCodes(CodeId pcodeId) {
    StringBuffer sql = new StringBuffer();
    sql.append("select code_id,DECODE ");
    sql.append("  from code ");
    sql.append(" where pcode_id = :pcodeId ");
    sql.append("   and useyn = 'Y' ");

    Map<String,String> param = Map.of("pcodeId",pcodeId.name());

    List<CodeDTO> codeDTOS = template.query(
        sql.toString(),
        param,
        BeanPropertyRowMapper.newInstance(CodeDTO.class));

    return codeDTOS;
  }
}
