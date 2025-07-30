package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.bbsUpload.svc.BbsUploadSVC;
import com.KDT.mosi.domain.entity.board.BbsUpload;
import com.KDT.mosi.domain.entity.board.UploadResult;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bbs/upload")
@RequiredArgsConstructor
public class ApiBbsUploadController {
  private final BbsUploadSVC bbsUploadSVC;
  private static final List<String> IMG_EXT = List.of(".png", ".jpg", ".jpeg", ".gif");
  @Value("${upload.path}")       // C:/KDT/projects/uploads/bbs
  private String uploadPath;
  @Value("${upload.url-prefix}")
  private String urlPrefix;

  @GetMapping("/{bbsId}/images")
  public ResponseEntity<ApiResponse<List<UploadResult>>> getInlineImages(@PathVariable("bbsId") Long bbsId) {
    List<UploadResult> images = bbsUploadSVC.findInlineByBbsIdOrderBySort(bbsId)
        .stream()
        .map(u -> new UploadResult(
            u.getUploadId(),
            urlPrefix + "/" + u.getSavedName(),   // ★ 수정
            u.getUploadGroup(),
            u.getOriginalName(),
            0L))
        .toList();
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, images));
  }

  @GetMapping("/{bbsId}/attachments")
  public ResponseEntity<ApiResponse<List<UploadResult>>> getAttachments(@PathVariable("bbsId") Long bbsId) {
    List<BbsUpload> list = bbsUploadSVC.findAttachmentsByBbsIdOrderBySort(bbsId);
    List<UploadResult> attachments = new ArrayList<>(list.size());

    for (BbsUpload u : list) {
      Path file = Paths.get(uploadPath).resolve(u.getSavedName()).normalize();
      long size;
      try {
        size = Files.size(file);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      String downloadUrl = urlPrefix + "/download/" + u.getUploadId();
      attachments.add(new UploadResult(
          u.getUploadId(),
          downloadUrl,
          u.getUploadGroup(),
          u.getOriginalName(),
          size
      ));
    }

    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, attachments));
  }

  /**
   * 본문 이미지 업로드
   */
  @PostMapping(
      value = "/images",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ApiResponse<List<UploadResult>>> uploadInline(
      @RequestParam(value="uploadGroup", required=false) Long uploadGroup,
      @RequestParam("files") List<MultipartFile> files
  ) {
    if (files == null || files.isEmpty()) return ResponseEntity.badRequest().build();
    List<UploadResult> results = bbsUploadSVC.saveAll(uploadGroup, "INLINE", files);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ApiResponseCode.SUCCESS, results));
  }

  /**
   * 첨부파일 업로드
   */
  @PostMapping(value = "/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<List<UploadResult>>> uploadAttachments(
      @RequestParam(value="uploadGroup", required=false) Long uploadGroup,
      @RequestParam("files") List<MultipartFile> files
  ) {
    if (files == null || files.isEmpty()) return ResponseEntity.badRequest().build();
    List<UploadResult> results = bbsUploadSVC.saveAll(uploadGroup, "ATTACHMENT", files);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.of(ApiResponseCode.SUCCESS, results));
  }


  /**
   * 개별 업로드 아이템 삭제
   */
  @DeleteMapping("/del/{uploadId}")
  public ResponseEntity<ApiResponse<Void>> deleteUpload(
      @PathVariable("uploadId") Long uploadId
  ) {
    bbsUploadSVC.deleteById(uploadId);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, null));
  }

  /**
   * 게시글의 모든 업로드 삭제
   */
  @DeleteMapping("/del/all/{bbsId}")
  public ResponseEntity<ApiResponse<Void>> deleteAllUploads(@PathVariable("bbsId") Long bbsId) {
    bbsUploadSVC.deleteByBbsId(bbsId);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, null));
  }

  /**
   * 업로드 순서 재정렬
   * 요청 바디: [{ "uploadId":1, "sortOrder":0 }, ...]
   */
  @PutMapping("/order")
  public ResponseEntity<Void> reorderUploads(
      @RequestBody List<Map<String, Integer>> orders
  ) {
    orders.forEach(item -> {
      Long uploadId = item.get("uploadId").longValue();
      int sortOrder = item.get("sortOrder");
      bbsUploadSVC.updateSortOrder(uploadId, sortOrder);
    });
    return ResponseEntity.noContent().build();
  }

  /**
   * 게시글의 첫 번째 본문 이미지(썸네일) 반환
   */
  @GetMapping("/{bbsId}/thumbnail")
  public ResponseEntity<ApiResponse<UploadResult>> getThumbnail(@PathVariable("bbsId") Long bbsId) {
    return bbsUploadSVC.findThumbnail(bbsId, "ATTACHMENT")
        .map(th -> ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, th)))
        .orElseGet(() -> ResponseEntity.ok(ApiResponse.of(ApiResponseCode.NO_DATA, null)));
  }

  @GetMapping("/attachments/{uploadId}")
  public ResponseEntity<Resource> downloadAttachment(@PathVariable("uploadId") Long uploadId) throws IOException {
    BbsUpload u = bbsUploadSVC.findById(uploadId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    Path path = Paths.get(uploadPath).resolve(u.getSavedName());
    long size = Files.size(path);

    Resource resource = new UrlResource(path.toUri());

    String filename = URLEncoder.encode(u.getOriginalName(), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(size)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + filename + "\"")
        .body(resource);
  }

  /**
   * bbsId에 연결되지 않은(uploadGroup) ID 목록 조회
   */
  @GetMapping("/groups/unlinked/{groupId}")
  public ResponseEntity<ApiResponse<List<Long>>> getUnlinkedUploadGroups(@PathVariable("groupId") Long groupId) {
    List<Long> groups = bbsUploadSVC.findUnlinkedUploadGroupIds(groupId);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, groups));
  }

}
