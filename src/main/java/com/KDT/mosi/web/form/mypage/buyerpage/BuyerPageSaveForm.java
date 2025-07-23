package com.KDT.mosi.web.form.mypage.buyerpage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BuyerPageSaveForm {

  // 회원 ID (필수) — hidden으로 전달받음
  @NotNull(message = "회원 ID는 필수입니다.")
  private Long memberId;

  // 프로필 이미지 (선택 사항)
  private MultipartFile imageFile;

  /** 닉네임 (2~30자, 중복 확인 대상) */
  @Size(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
  private String nickname;

  // 자기소개 (선택 사항, 최대 500자)
  @Size(max = 500, message = "자기소개는 500자 이내로 입력해주세요.")
  private String intro;

  // 최근 주문 상품명 (선택 사항, 최대 100자)
  @Size(max = 100, message = "최근 주문 상품명은 100자 이내로 입력해주세요.")
  private String recentOrder;

  // 적립 포인트 (선택 사항)
  private Integer point;
}
