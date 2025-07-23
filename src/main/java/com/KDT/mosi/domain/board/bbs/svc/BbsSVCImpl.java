package com.KDT.mosi.domain.board.bbs.svc;

import com.KDT.mosi.domain.board.bbs.dao.BbsDAO;
import com.KDT.mosi.domain.entity.board.Bbs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BbsSVCImpl implements BbsSVC{
  private final BbsDAO bbsDAO;

  @Override
  public Long save(Bbs bbs) {
    // ① 답글이라면 부모 글을 미리 조회해 규칙만 확인
    if (bbs.getPbbsId() != null) {
      Bbs parent = bbsDAO.findById(bbs.getPbbsId())
          .orElseThrow(() -> new IllegalArgumentException("부모 글 없음"));

      // ▸ 깊이 2 초과 금지
      if (parent.getBindent() >= 2) {
        throw new IllegalStateException("3단계 이상 답글은 허용되지 않습니다.");
      }
      // ▸ 닫힌 글(B0201 아님)에는 답글 금지
      if (!"B0201".equals(parent.getStatus())) {
        throw new IllegalStateException("닫힌 글에는 답글을 달 수 없습니다.");
      }
    }

    // 최근 10건 중복 제목·내용 검사
    if (bbsDAO.existsDuplicateRecent(bbs.getTitle(), bbs.getBcontent())) {
      throw new IllegalStateException("최근 10개 안에 동일한 제목/내용의 글이 이미 존재합니다.");
    }
    return bbsDAO.save(bbs);
  }

  @Override
  public List<Bbs> findAll() {
    return bbsDAO.findAll();
  }

  @Override
  public List<Bbs> findAll(int pageNo, int numOfRows) {
    return bbsDAO.findAll(pageNo, numOfRows);
  }

  @Override
  public int getTotalCount() {
    return bbsDAO.getTotalCount();
  }

  @Override
  public List<Bbs> findAll(String bcategory) {
    return bbsDAO.findAll(bcategory);
  }

  @Override
  public List<Bbs> findAll(String bcategory, int pageNo, int numOfRows) {
    return bbsDAO.findAll(bcategory,pageNo,numOfRows);
  }

  @Override
  public int getTotalCount(String bcategory) {
    return bbsDAO.getTotalCount(bcategory);
  }

  @Override
  public Optional<Bbs> findById(Long id) {
    return bbsDAO.findById(id);
  }

  @Override
  public int deleteById(Long id) {
    return bbsDAO.deleteById(id);
  }

  @Override
  public int deleteByIds(List<Long> ids) {
    return bbsDAO.deleteByIds(ids);
  }

  @Override
  public int updateById(Long bbsId, Bbs bbs) {
    return bbsDAO.updateById(bbsId,bbs);
  }

  @Override
  public int updateStep(Long bgroup, Bbs parentBbs) {
    return bbsDAO.updateStep(bgroup,parentBbs);
  }

  @Override
  public int increaseHit(Long id) {
    return bbsDAO.increaseHit(id);
  }

  @Override
  public Optional<Bbs> findTemporaryStorageById(Long member_id, Long pbbs_id) {
    return bbsDAO.findTemporaryStorageById(member_id, pbbs_id);
  }

  @Override
  public int deleteTemporaryStorage(Long memberId, Long pbbsId) {
    return bbsDAO.deleteTemporaryStorage(memberId, pbbsId);
  }

}
