package com.KDT.mosi.domain.board.bbs.dao;

import com.KDT.mosi.domain.entity.board.Bbs;

import java.util.List;
import java.util.Optional;

public interface BbsDAO {
  //게시글등록
  Long save(Bbs bbs);

  //게시글 모든 목록
  List<Bbs> findAll();

  //페이징 전체 게시글 목록
  List<Bbs> findAll(int pageNo, int numOfRows);

  //전체 게시글 수
  int getTotalCount();

  //카테고리별 게시글 모든 목록
  List<Bbs> findAll(String bcategory);

  //카테고리별 페이징 전체 게시글 목록
  List<Bbs> findAll(String bcategory,int pageNo, int numOfRows);

  //카테고리별 전체 게시글 수
  int getTotalCount(String bcategory);

  //게시글확인
  Optional<Bbs> findById(Long id);

  //게시글삭제(단건)
  int deleteById(Long id);

  //게시글삭제(여러건)
  int deleteByIds(List<Long> ids);

  //게시글수정
  int updateById(Long bbsId, Bbs bbs);

  //답글 순서 조정
  int updateStep(Long bgroup, Bbs parentBbs);

  //조회수 증가
  public int increaseHit(Long id);

  //중복확인
  public boolean existsDuplicateRecent(String title, String bcontent, int CHECK_CHAR_LEN);

  //게시글 임시저장 확인
  Optional<Bbs> findTemporaryStorageById(Long member_id, Long pbbs_id);

  //게시글 임시저장 삭제
  int deleteTemporaryStorage(Long memberId, Long pbbsId);

}
