package com.KDT.mosi.web.form.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductUploadForm {
    private Long memberId;
    private String nickname;

    private String category;
    private String title;
    private String guideYn;

    private Integer normalPrice;
    private Integer guidePrice;
    private Integer salesPrice;
    private Integer salesGuidePrice;
    private Integer totalDay;
    private Integer totalTime;
    private Integer reqMoney;

    private String sleepInfo;
    private String transportInfo;
    private String foodInfo;
    private String reqPeople;
    private String target;
    private String stucks;
    private String description;
    private String detail;
    private String priceDetail;
    private String gpriceDetail;
    private String status;

    // 최대 10장 이미지 업로드
    private List<MultipartFile> productImages;

    // 문서 첨부파일
    private MultipartFile documentFile;

    // 지도 코스포인트 리스트
    private List<ProductCoursePointForm> coursePoints;
}