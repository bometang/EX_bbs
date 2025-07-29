package com.KDT.mosi.domain.board.bbsUpload.svc;

import com.KDT.mosi.domain.entity.board.BbsUpload;
import com.KDT.mosi.domain.entity.board.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BbsUploadSVC {
  /** 단일 메타 저장 (upload_id 리턴) */
  Long save(BbsUpload upload,MultipartFile file);

  /** 여러 건 메타 저장 → id/url 묶음 리턴 */
  List<UploadResult> saveAll(Long bbsId,String fileType,List<MultipartFile> files);

  /** 특정 게시글의 INLINE 이미지만 sort_order 순으로 조회 */
  List<BbsUpload> findInlineByBbsIdOrderBySort(Long bbsId);

  /** 특정 게시글의 ATTACHMENT 파일만 sort_order 순으로 조회 */
  List<BbsUpload> findAttachmentsByBbsIdOrderBySort(Long bbsId);

  /** 특정 게시글·파일 타입의 현재 최대 sort_order 값 조회 */
  int getMaxSortOrder(Long bbsId, String fileType);

  /** 개별 이미지/파일 삭제 */
  void deleteById(Long uploadId);

  /** 게시글 전체 이미지·파일 일괄 삭제 */
  int deleteByBbsId(Long bbsId);

  /** image_id에 대응하는 sort_order 업데이트 */
  int updateSortOrder(Long uploadId, int sortOrder);

  // 삭제시 순서 조정
  int decrementSortOrders(Long bbsId, String fileType, int fromOrder);

  // 업로드 찾기
  Optional<BbsUpload> findById(Long uploadId);

  // 게시글 저장시 bbsId 업데이트
  int bindGroupToBbs(Long bbsId, Long uploadGroup);

  // 이미지 하나만 가져오기
  Optional<UploadResult> findThumbnail(Long bbsId, String fileType);

  List<Long> findUnlinkedUploadGroupIds(Long groupId);

}
