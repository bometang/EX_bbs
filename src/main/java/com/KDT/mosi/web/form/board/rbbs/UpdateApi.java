package com.KDT.mosi.web.form.board.rbbs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateApi {
  private Long bbsId;
  @NotBlank(message = "내용은 필수 입니다.")
  private String bcontent;
  private LocalDateTime udateDate;
  private Long memberId;
}
