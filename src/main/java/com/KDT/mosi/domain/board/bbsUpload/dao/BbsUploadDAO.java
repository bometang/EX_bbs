package com.KDT.mosi.domain.board.bbsUpload.dao;

import com.KDT.mosi.domain.entity.board.BbsUpload;

import java.util.List;
import java.util.Optional;

public interface BbsUploadDAO {

  /** 단일 메타 저장 (image_id 리턴) */
  Long save(BbsUpload upload);
//
//  /** 여러 건 메타 저장 → id/url 묶음 리턴 */
//  List<UploadResult> saveAll(List<BbsUpload> uploads);

  /** 특정 게시글의 INLINE 이미지만 sort_order 순으로 조회 */
  List<BbsUpload> findInlineByBbsIdOrderBySort(Long bbsId);

  /** 특정 게시글의 ATTACHMENT 파일만 sort_order 순으로 조회 */
  List<BbsUpload> findAttachmentsByBbsIdOrderBySort(Long bbsId);

  /** 특정 게시글·파일 타입의 현재 최대 sort_order 값 조회 */
  int getMaxSortOrder(Long bbsId, String fileType);

  /** 개별 이미지/파일 삭제 */
  int deleteById(Long imageId);

  /** 게시글 전체 이미지·파일 일괄 삭제 */
  int deleteByBbsId(Long bbsId);

  /** image_id에 대응하는 sort_order 업데이트 */
  int updateSortOrder(Long imageId, int sortOrder);

  // 삭제시 순서 조정
  int decrementSortOrders(Long bbsId, String fileType, int fromOrder);

  // 업로드 찾기
  Optional<BbsUpload> findById(Long uploadId);

  // 게시글 저장시 bbsId 업데이트
  int bindGroupToBbs(Long bbsId, Long uploadGroup);

  // 업로드 파일 임시 그룹id 발급
  Long createUploadGroup();

  // 이미지 하나만 가져오기
  Optional<BbsUpload> findFirstImageByBbsId(Long bbsId, String fileTYpe);
}
