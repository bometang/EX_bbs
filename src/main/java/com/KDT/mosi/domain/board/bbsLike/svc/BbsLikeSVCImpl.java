package com.KDT.mosi.domain.board.bbsLike.svc;

import com.KDT.mosi.domain.board.bbsLike.dao.BbsLikeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BbsLikeSVCImpl implements BbsLikeSVC {
  private final BbsLikeDAO bbsLikeDAO;

  @Override
  public String toggleLike(Long id, Long bbsId) {
    return bbsLikeDAO.toggleLike(id,bbsId);
  }

  @Override
  public int getTotalCountLike(Long bbsId) {
    return bbsLikeDAO.getTotalCountLike(bbsId);
  }

  @Override
  public boolean getLike(Long bbsId, Long memberId) {
    return bbsLikeDAO.getLike(bbsId, memberId);
  }
}
