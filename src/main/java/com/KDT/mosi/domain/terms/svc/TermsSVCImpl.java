package com.KDT.mosi.domain.terms.svc;

import com.KDT.mosi.domain.entity.Terms;
import com.KDT.mosi.domain.terms.dao.TermsDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsSVCImpl implements TermsSVC {

  private final TermsDAO termsDAO;

  @Override
  public List<Terms> findAll() {
    return termsDAO.findAll();
  }
}
