package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.rbbsLike.svc.RBbsLikeSVC;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/bbs/comments/{rbbsId}/likes")
@RequiredArgsConstructor
public class ApiRBbsLikeController {

  private final RBbsLikeSVC rbbsLikeSVC;

  /**
   * 좋아요 토글 (누르면 있으면 취소, 없으면 추가)
   * POST /api/bbs/{bbsId}/likes
   */
  @PostMapping
  public ResponseEntity<ApiResponse<String>> toggle(
      @PathVariable("rbbsId") Long rbbsId,
      HttpSession session
  ) {
    Member loginMember = (Member) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

    String result = rbbsLikeSVC.toggleLike(loginMember.getMemberId(), rbbsId);
    // "CREATED" 또는 "DELETED" 반환
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, result));
  }

  /**
   * 해당 게시글의 좋아요 수 조회
   * GET /api/bbs/{bbsId}/likes/count
   */
  @GetMapping("/count")
  public ResponseEntity<ApiResponse<Integer>> count(@PathVariable("rbbsId") Long rbbsId) {
    int total = rbbsLikeSVC.getTotalCountLike(rbbsId);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, total));
  }
}
