package com.KDT.mosi.domain.board.rbbs.svc;

import com.KDT.mosi.domain.board.rbbs.dao.RbbsDAO;
import com.KDT.mosi.domain.entity.board.Rbbs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RbbsSVCImpl implements RbbsSVC {
  private final RbbsDAO rbbsDAO;

  @Override
  public Long save(Rbbs rbbs) {
    return rbbsDAO.save(rbbs);
  }

  @Override
  public List<Rbbs> findAll(Long id) {
    return rbbsDAO.findAll(id);
  }

  @Override
  public List<Rbbs> findAll(Long id, int pageNo, int numOfRows) {
    return rbbsDAO.findAll(id,pageNo,numOfRows);
  }

  @Override
  public int getTotalCount(Long id) {
    return rbbsDAO.getTotalCount(id);
  }

  @Override
  public Optional<Rbbs> findById(Long id) {
    return rbbsDAO.findById(id);
  }

  @Override
  public int deleteById(Long id) {
    return rbbsDAO.deleteById(id);
  }

  @Override
  public int updateById(Long bbsCommentId, Rbbs rbbs) {
    return rbbsDAO.updateById(bbsCommentId,rbbs);
  }

  @Override
  public int updateStep(Long bgroup, Rbbs pbbsComment) {
    return rbbsDAO.updateStep(bgroup,pbbsComment);
  }
}
