package com.KDT.mosi.domain.board.bbsLike.dao;

public interface BbsLikeDAO {

  // 좋아요 클릭
  String toggleLike(Long id, Long bbsId);

  // 게시글의 좋아요 갯수
  int getTotalCountLike(Long bbsId);

  //게시글 좋아요 유무
  boolean getLike(Long bbsId,Long memberId);
}
