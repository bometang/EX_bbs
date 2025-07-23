package com.KDT.mosi.web.controller.board;

import com.KDT.mosi.domain.board.bbs.svc.BbsSVC;
import com.KDT.mosi.domain.board.bbsUpload.svc.BbsUploadSVC;
import com.KDT.mosi.domain.common.CodeId;
import com.KDT.mosi.domain.common.svc.CodeSVC;
import com.KDT.mosi.domain.dto.CodeDTO;
import com.KDT.mosi.domain.entity.Member;
import com.KDT.mosi.domain.entity.board.Bbs;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.board.bbs.SaveApi;
import com.KDT.mosi.web.form.board.bbs.UpdateApi;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/bbs")
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class ApiBbsController {
  private final BbsSVC bbsSVC;
  private final CodeSVC codeSVC;
  private final BbsUploadSVC bbsUploadSVC;


  //게시글 추가
  @PostMapping
  public ResponseEntity<ApiResponse<Bbs>> add(
      @RequestBody @Valid SaveApi saveApi,
      HttpSession session
  ) {
    Long memberId = (Long) session.getAttribute("loginMemberId");
    saveApi.setMemberId(memberId);
    Bbs bbs = new Bbs();
    BeanUtils.copyProperties(saveApi, bbs);
    Long id = bbsSVC.save(bbs);
    if (saveApi.getUploadGroup() != null) {
      bbsUploadSVC.bindGroupToBbs(id,saveApi.getUploadGroup());
      log.info("saveApi.getUploadGroup = {}", saveApi.getUploadGroup());
    }else{
      log.info("NOONONONONOsaveApi.getUploadGroup = {}", saveApi.getUploadGroup());
    }
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow();

    ApiResponse<Bbs> postBbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, findedBbs);

    return ResponseEntity.status(HttpStatus.CREATED).body(postBbsApiResponse);
  }

  /**
   * 1) 임시저장 존재 여부 확인
   *    GET /api/bbs/temp/check?pbbsId={pbbsId}
   */
  @GetMapping("/temp/check")
  public ResponseEntity<ApiResponse<Boolean>> hasTemp(
      @RequestParam(name = "pbbsId", required = false) Long pbbsId,
      HttpSession session
  ) {
    Long memberId = (Long) session.getAttribute("loginMemberId");
    boolean exists = bbsSVC.findTemporaryStorageById(memberId, pbbsId).isPresent();
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, exists));
  }

  /**
   * 2) 임시저장 로드
   *    GET /api/bbs/temp/load?pbbsId={pbbsId}
   */
  @GetMapping("/temp/load")
  public ResponseEntity<ApiResponse<Bbs>> loadTemp(
      @RequestParam(name = "pbbsId", required = false) Long pbbsId,
      HttpSession session
  ) {
    Long memberId = (Long) session.getAttribute("loginMemberId");
    Optional<Bbs> tempOpt = bbsSVC.findTemporaryStorageById(memberId, pbbsId);
    Bbs temp = tempOpt.get();

    // 2) 불러온 뒤 곧바로 삭제
//    bbsSVC.deleteTemporaryStorage(memberId, pbbsId);

    // 3) 로드된 내용 반환
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, temp));
  }

  /**
   * 3) 임시저장 삭제
   *    DELETE /api/bbs/temp?pbbsId={pbbsId}
   */
  @DeleteMapping("/temp")
  public ResponseEntity<ApiResponse<Void>> deleteTemp(
      @RequestParam(name = "pbbsId", required = false) Long pbbsId,
      HttpSession session
  ) {
    Long memberId = (Long) session.getAttribute("loginMemberId");
    bbsSVC.deleteTemporaryStorage(memberId, pbbsId);
    log.info("삭제 완료");
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, null));
  }


  //게시글 조회
  @GetMapping("/{id}")
//  @ResponseBody   // 응답메세지 body에 자바 객체를 json포맷 문자열로 변환
  public ResponseEntity<ApiResponse<Bbs>> findById(@PathVariable("id") Long id) {

    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow();  // 찾고자하는 게시글이 없으면 NoSuchElementException 예외발생

    ApiResponse<Bbs> bbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, findedBbs);

    return ResponseEntity.ok(bbsApiResponse);  //상태코드 200, 응답메세지Body:bbsApiResponse객채가 json포맷 문자열로 변환됨
  }

  //게시글 조회
  @GetMapping("/{id}/view")
//  @ResponseBody   // 응답메세지 body에 자바 객체를 json포맷 문자열로 변환
  public ResponseEntity<ApiResponse<Bbs>> view(@PathVariable("id") Long id) {

    bbsSVC.increaseHit(id);
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow();  // 찾고자하는 게시글이 없으면 NoSuchElementException 예외발생

    ApiResponse<Bbs> bbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, findedBbs);

    return ResponseEntity.ok(bbsApiResponse);  //상태코드 200, 응답메세지Body:bbsApiResponse객채가 json포맷 문자열로 변환됨
  }

  //게시글 수정      //   PATCH   /bbs/{id} =>  PATCH http://localhost:9080/api/bbs/{id}
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Bbs>> updateById(
      @PathVariable("id") Long id,
      @RequestBody @Valid UpdateApi updateApi, // 요청메세지의 json포맷의 문자열을 자바 객체로 변환
      HttpSession session
  ) {

    //1) 게시글조회
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedbbs = optionalBbs.orElseThrow(
        ()->new NoSuchElementException("게시글번호 : " + id + " 를 찾을 수 없습니다.")
    );  // 찾고자하는 게시글이 없으면 NoSuchElementException 예외발생

    // 2) 로그인 정보 꺼내기
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    Member loginMember = (Member) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }


    log.info("loginMemberId = {}", loginMemberId);
    // 3) 작성자 확인
    boolean isAuthor    = findedbbs.getMemberId().equals(loginMemberId);
    if (!isAuthor) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 댓글을 수정할 수 있습니다.");
    }

    //4) 게시글수정
    Bbs bbs = new Bbs();
    BeanUtils.copyProperties(updateApi, bbs);
    bbs.setBbsId(id);
    int updatedRow = bbsSVC.updateById(id, bbs);


    //5) 수정된게시글 조회
    optionalBbs = bbsSVC.findById(id);
    Bbs updatedBbs = optionalBbs.orElseThrow();

    //6) REST API 응답 표준 메시지 생성
    ApiResponse<Bbs> bbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, updatedBbs);

    //7) HTTP 응답 메세지 생성
    return ResponseEntity.ok(bbsApiResponse);
  }

  //게시글 삭제      //   DELETE  /bbs/{id} =>  DELETE http://localhost:9080/api/bbs/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Bbs>> deleteById(
      @PathVariable("id") Long id,
      HttpSession session
  ) {
    //1) 게시글조회
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow(
        ()->new NoSuchElementException("게시글번호 : " + id + " 를 찾을 수 없습니다.")
    );  // 찾고자하는 게시글이 없으면 NoSuchElementException 예외발생

    // 2) 로그인 정보 꺼내기
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    Member loginMember = (Member) session.getAttribute("loginMember");
    if (loginMember == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }


    // 3) 작성자 확인
    boolean isAuthor    = findedBbs.getMemberId().equals(loginMemberId);

    if (!isAuthor) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 댓글을 수정할 수 있습니다.");
    }




    //2) 게시글 삭제
    int deletedRow = bbsSVC.deleteById(id);

    //3) REST API 표준 응답 생성
    ApiResponse<Bbs> bbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, findedBbs);

    //4) HTTP응답 메세지 생성
    return ResponseEntity.ok(bbsApiResponse);
  }

  //게시글 목록      //   GET     /bbs      =>  GET http://localhost:9080/api/bbs
  @GetMapping
//  @ResponseBody
  public ResponseEntity<ApiResponse<List<Bbs>>> findAll() {

    List<Bbs> list = bbsSVC.findAll();
    ApiResponse<List<Bbs>> listApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, list);

    return ResponseEntity.ok(listApiResponse);
  }

  //게시글 목록-페이징      //   GET     /bbs      =>  GET http://localhost:9090/api/bbs/paging?pageNo=1&numOfRows=10
  @GetMapping("/paging")
//  @ResponseBody
  public ResponseEntity<ApiResponse<List<Bbs>>> findAll(
      @RequestParam(value="pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(value="numOfRows", defaultValue = "10") Integer numOfRows
  ) {
    log.info("pageNo={},numOfRows={}", pageNo, numOfRows);
    //게시글목록 가져오기
    List<Bbs> list = bbsSVC.findAll(pageNo, numOfRows);
    //게시글 총건수 가져오기
    int totalCount = bbsSVC.getTotalCount();
    //REST API 표준 응답 만들기
    ApiResponse<List<Bbs>> listApiResponse = ApiResponse.of(
        ApiResponseCode.SUCCESS,
        list,
        new ApiResponse.Paging(pageNo, numOfRows, totalCount)
    );
    return ResponseEntity.ok(listApiResponse);
  }

  //전체 건수 가져오기      //   GET   /bbs/totCnt =>  GET http://localhost:9080/api/bbs/totCnt
  @GetMapping("/totCnt")
  public ResponseEntity<ApiResponse<Integer>> totalCount() {

    int totalCount = bbsSVC.getTotalCount();
    ApiResponse<Integer> bbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, totalCount);

    return ResponseEntity.ok(bbsApiResponse);  //상태코드 200, 응답메세지Body:bbsApiResponse객채가 json포맷 문자열로 변환됨
  }

  /**
   * 게시판 카테고리 리스트 조회 (CodeId.B01)
   */
  @GetMapping("/categories")
  public ResponseEntity<ApiResponse<List<CodeDTO>>> categories() {
    List<CodeDTO> list = codeSVC.getCodes(CodeId.B01);
    return ResponseEntity
        .ok(ApiResponse.of(ApiResponseCode.SUCCESS, list));
  }


}
