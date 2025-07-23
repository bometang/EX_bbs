package com.KDT.mosi.domain.board.bbsUpload.svc;

import com.KDT.mosi.domain.board.bbsUpload.dao.BbsUploadDAO;
import com.KDT.mosi.domain.entity.board.BbsUpload;
import com.KDT.mosi.domain.entity.board.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BbsUploadSVCImpl implements BbsUploadSVC{
  private static final Set<String> ALLOWED_INLINE_EXT = Set.of("png","jpg","jpeg","gif");
  private static final Set<String> ALLOWED_ATTACHMENT_EXT = Set.of("pdf", "doc", "docx", "xls", "xlsx", "zip", "txt","png", "jpg", "jpeg", "gif");
  private final BbsUploadDAO bbsUploadDAO;

  @Value("${upload.path}")
  private String uploadPath;
  @Value("${upload.url-prefix}")
  private String urlPrefix;

  @Override
  @Transactional
  public Long save(BbsUpload upload,MultipartFile file) {
    // 1) 원본 파일명
    String originalName = file.getOriginalFilename();

    // 2) 확장자, 베이스 네임 분리 (Spring Util 사용)
    String ext      = StringUtils.getFilenameExtension(originalName);
    String baseName = StringUtils.stripFilenameExtension(originalName);

    if (ext == null) {
      throw new IllegalArgumentException("확장자를 알 수 없는 파일입니다.");
    }
    ext = ext.toLowerCase();

    switch (upload.getFileType()){
      case "ATTACHMENT":
        if (!ALLOWED_ATTACHMENT_EXT.contains(ext)) {
          throw new IllegalArgumentException("허용되지 않은 확장자입니다: " + ext);
        }
        break;
      case "INLINE":
        if (!ALLOWED_INLINE_EXT.contains(ext)) {
          throw new IllegalArgumentException("허용되지 않은 확장자입니다: " + ext);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown fileType: " + upload.getFileType());
    }


    // 3) 타임스탬프 + UUID 조합으로 유니크한 저장명 생성
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String uuid      = UUID.randomUUID().toString().replace("-", "");
    String cleanBase = baseName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    String safeBase  = URLEncoder.encode(cleanBase, StandardCharsets.UTF_8);
    String savedName = String.format("%s_%s.%s",
        timestamp, uuid,
        ext != null ? ext : "");

    // 4) 디스크에 실제 저장 (uploadPath는 application.yml 의 upload.path)
    File dir = new File(uploadPath);
    if (!dir.exists()) dir.mkdirs();
    File target = new File(dir, savedName);
    try {
      file.transferTo(target);
    } catch (IOException | IllegalStateException e) {
      e.printStackTrace();
    }

    // 5) 엔티티에 메타 정보 세팅
    upload.setOriginalName(originalName);
    upload.setSavedName(savedName);
    // 클라이언트가 URL로 접근할 경로; ResourceHandler 에 매핑된 prefix를 포함
    upload.setFilePath(urlPrefix + "/" + savedName);
    log.warn("★ SET filePath={}", upload.getFilePath());
    // 6) DAO 호출하여 DB에 메타 저장 후, 생성된 PK 리턴
    return bbsUploadDAO.save(upload);
  }

  @Override
  @Transactional
  public List<UploadResult> saveAll(Long uploadGroup,String fileType,List<MultipartFile> files) {
    // 1) 현재 최대 sort_order 구하고
    int nextOrder;
    if(uploadGroup != null){
      nextOrder = bbsUploadDAO.getMaxSortOrder(uploadGroup, fileType) + 1;
    }else {
      uploadGroup = bbsUploadDAO.createUploadGroup();
      nextOrder =0;
    }

    List<UploadResult> results = new ArrayList<>(files.size());
    for (MultipartFile file : files) {
      // 2) BbsUpload 엔티티 준비
      BbsUpload u = new BbsUpload();
      u.setUploadGroup(uploadGroup);
      u.setFileType(fileType);
      u.setSortOrder(nextOrder++);
      Long id = save(u, file);
      String publicUrl = urlPrefix + "/" + u.getSavedName();
      results.add(new UploadResult(id, publicUrl, uploadGroup, file.getOriginalFilename()));
      log.info("★ publicUrl={}", publicUrl);
    }
    return results;
  }

  @Override
  public List<BbsUpload> findInlineByBbsIdOrderBySort(Long bbsId) {
    return bbsUploadDAO.findInlineByBbsIdOrderBySort(bbsId);
  }

  @Override
  public List<BbsUpload> findAttachmentsByBbsIdOrderBySort(Long bbsId) {
    return bbsUploadDAO.findAttachmentsByBbsIdOrderBySort(bbsId);
  }

  @Override
  public int getMaxSortOrder(Long uploadGroup, String fileType) {
    return bbsUploadDAO.getMaxSortOrder(uploadGroup,fileType);
  }

  @Override
  @Transactional
  public void deleteById(Long uploadId) {
    // 1) 삭제 전 메타(특히 bbsId, fileType, sortOrder) 조회
    BbsUpload target = bbsUploadDAO.findById(uploadId)
        .orElseThrow(() -> new NoSuchElementException("이미지를 찾을 수 없습니다: " + uploadId));
    Long    uploadGroup      = target.getUploadGroup();
    String  fileType   = target.getFileType();
    int     order      = target.getSortOrder();

    // 2) DB에서 해당 항목 삭제
    bbsUploadDAO.deleteById(uploadId);

    // 3) 뒤 항목들에 대해 sort_order 당기기
    bbsUploadDAO.decrementSortOrders(uploadGroup, fileType, order);

    // 4) (선택) 디스크에 저장된 파일도 함께 삭제
    File f = new File(uploadPath, target.getSavedName());
    if (f.exists() && !f.delete()) {
      log.warn("파일 삭제 실패: {}", f.getAbsolutePath());
    }
  }

  @Override
  @Transactional
  public int deleteByBbsId(Long bbsId) {
    // 1) 삭제 대상 메타(파일명) 모두 조회
    List<BbsUpload> all = bbsUploadDAO.findInlineByBbsIdOrderBySort(bbsId);
    all.addAll(bbsUploadDAO.findAttachmentsByBbsIdOrderBySort(bbsId));

    // 2) 디스크에서 파일 지우기
    for (BbsUpload u : all) {
      File f = new File(uploadPath, u.getSavedName());
      if (f.exists() && !f.delete()) {
        log.warn("파일 삭제 실패: {}", f.getAbsolutePath());
      }
    }

    // 3) DB 레코드 삭제
    return bbsUploadDAO.deleteByBbsId(bbsId);
  }

  @Override
  public int updateSortOrder(Long uploadId, int sortOrder) {
    return bbsUploadDAO.updateSortOrder(uploadId,sortOrder);
  }

  @Override
  public int decrementSortOrders(Long bbsId, String fileType, int fromOrder) {
    return bbsUploadDAO.decrementSortOrders(bbsId,fileType,fromOrder);
  }

  @Override
  public Optional<BbsUpload> findById(Long uploadId) {
    return bbsUploadDAO.findById(uploadId);
  }

  @Override
  public int bindGroupToBbs(Long bbsId, Long uploadGroup) {
    return bbsUploadDAO.bindGroupToBbs(bbsId, uploadGroup);
  }

  @Override
  public Optional<UploadResult> findThumbnail(Long bbsId, String fileType) {
    return bbsUploadDAO.findFirstImageByBbsId(bbsId, fileType)
        .map(u -> new UploadResult(
            u.getUploadId(),
            urlPrefix + "/" + u.getSavedName(),
            u.getUploadGroup(),
            u.getOriginalName()
        ));
  }
}
