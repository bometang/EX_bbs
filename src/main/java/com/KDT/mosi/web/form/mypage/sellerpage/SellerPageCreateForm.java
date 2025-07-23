package com.KDT.mosi.web.form.mypage.sellerpage;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerPageCreateForm {

  // 셀러 페이지 고유 ID (hidden으로 전달)
  private Long pageId;

  // 회원 ID (수정 불가, hidden)
  private Long memberId;

  // 새 프로필 이미지 파일 (선택 시 덮어씀)
  private MultipartFile imageFile;

  // 자기소개 (최대 500자)
  @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
  private String intro;

  // 별명
  @Size(max = 10, message = "닉네임은 10자 이내로 입력해주세요.")
  private String nickname;


}
