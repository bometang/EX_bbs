package com.KDT.mosi.web.form.board.rbbs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveApi {
  @NotBlank(message = "내용은 필수 입니다.")
  private String bcontent;

  private String memberId;
  private String status;

  private Long prbbsId;
  private Long bgroup;
  private Long step;
  @Max(value = 2, message = "들여쓰기 단계는 최대 2까지만 가능합니다.")
  private Long bindent;
}
