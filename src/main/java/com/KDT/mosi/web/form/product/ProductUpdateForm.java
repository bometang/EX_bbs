package com.KDT.mosi.web.form.product;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductUpdateForm {

  @NotNull
  private Long productId;

  @NotNull
  private Long memberId;

  @NotNull
  private String nickname;

  @Size(max = 30)
  private String category;

  @NotBlank
  @Size(max = 100)
  private String title;

  @Pattern(regexp = "[YN]")
  private String guideYn;

  @Min(0)
  private Integer normalPrice;

  @Min(0)
  private Integer guidePrice;

  @Min(0)
  private Integer salesPrice;

  @Min(0)
  private Integer salesGuidePrice;

  @Min(0)
  private Integer totalDay;

  @Min(0)
  private Integer totalTime;

  @Min(0)
  private Integer reqMoney;

  @Pattern(regexp = "[YN]")
  private String sleepInfo;

  @Size(max = 150)
  private String transportInfo;

  @Pattern(regexp = "[YN]")
  private String foodInfo;

  @Size(max = 45)
  private String reqPeople;

  @Size(max = 45)
  private String target;

  @Size(max = 90)
  private String stucks;

  @Size(max = 500)
  private String description;

  @Size(max = 3000)
  private String detail;

  @Size(max = 255)
  private String fileName;

  @Size(max = 50)
  private String fileType;

  private Long fileSize;

  private byte[] fileData;

  @Size(max = 15)
  private String status;

  @Size(max = 450)
  private String priceDetail;

  @Size(max = 450)
  private String gpriceDetail;

  // 이미지 업로드: 신규 이미지 리스트
  private List<MultipartFile> imageFiles;

  // 이미지 삭제용 ID 리스트
  private List<Long> deleteImageIds;

  // 코스포인트 리스트(신규/수정용)
  private List<ProductCoursePointForm> coursePoints;

  // 코스포인트 삭제용 ID 리스트
  private List<Long> deleteCoursePointIds;
}