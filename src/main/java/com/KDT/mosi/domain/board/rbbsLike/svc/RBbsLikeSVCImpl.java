package com.KDT.mosi.domain.board.rbbsLike.svc;

import com.KDT.mosi.domain.board.rbbsLike.dao.RBbsLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RBbsLikeSVCImpl implements RBbsLikeSVC {
  private final RBbsLikeDAO bbsLikeDAO;

  @Override
  public String toggleLike(Long id, Long rbbsId) {
    return bbsLikeDAO.toggleLike(id,rbbsId);
  }

  @Override
  public int getTotalCountLike(Long rbbsId) {
    return bbsLikeDAO.getTotalCountLike(rbbsId);
  }

  @Override
  public boolean getLike(Long rbbsId, Long memberId) {
    return bbsLikeDAO.getLike(rbbsId, memberId);
  }
}
