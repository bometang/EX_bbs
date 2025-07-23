package com.KDT.mosi.domain.board.rbbs.svc;

import com.KDT.mosi.domain.entity.board.Rbbs;

import java.util.List;
import java.util.Optional;

public interface RbbsSVC {
  //댓글등록
  Long save(Rbbs rbbs);

  //댓글 목록
  List<Rbbs> findAll(Long id);

  List<Rbbs> findAll(Long id, int pageNo, int numOfRows);

  int getTotalCount(Long id);

  //댓글확인
  Optional<Rbbs> findById(Long id);

  //댓글삭제(단건)
  int deleteById(Long id);

  //댓글수정
  int updateById(Long bbsCommentId, Rbbs rbbs);

  //답글 순서 조정
  int updateStep(Long bgroup, Rbbs pbbsComment);
}
