package com.KDT.mosi.web.form.board.bbs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveApi {
  @NotBlank(message = "제목은 필수 입니다.")
  @Size(min=1,max=33,message = "제목은 33자를 초과할 수 없습니다.")
  private String title;

  @NotBlank(message = "내용은 필수 입니다.")
  private String bcontent;

  private Long memberId;

  private String bcategory;
  private String status;

  private Long pbbsId;
  private Long bgroup;
  private Long step;
  @Max(value = 2, message = "들여쓰기 단계는 최대 2까지만 가능합니다.")
  private Long bindent;

  private Long uploadGroup;
}
