package com.KDT.mosi.web.form.mypage.sellerpage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SellerPageSaveForm {

  // 회원 ID (필수) — hidden으로 전달받음
  @NotNull(message = "회원 ID는 필수입니다.")
  private Long memberId;

  // 프로필 이미지 (선택 사항)
  private MultipartFile imageFile;

  // 자기소개 (선택 사항, 최대 500자)
  @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
  private String intro;

  // 별명
  @Size(max = 10)
  private String nickname;

  // 최근 판매 상품명 (선택 사항, 최대 100자)
  @Size(max = 100, message = "최근 판매 상품명은 100자 이내로 입력해주세요.")
  private String recentProduct;

  // 누적 판매량 또는 판매 지수 (선택 사항)
  private Integer salesCount;
}
