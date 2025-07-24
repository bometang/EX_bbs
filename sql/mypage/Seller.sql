-- =======================================
-- ✅ 1. 기존 테이블 및 시퀀스 삭제
-- =======================================
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE SELLER_PAGE CASCADE CONSTRAINTS';
EXCEPTION
  WHEN OTHERS THEN NULL;
END;


DROP SEQUENCE SELLER_PAGE_SEQ;


-- =======================================
-- ✅ 2. SELLER_PAGE 테이블 생성
-- =======================================
CREATE TABLE SELLER_PAGE (
  PAGE_ID           NUMBER(10) PRIMARY KEY,                 -- 마이페이지 ID
  MEMBER_ID         NUMBER(10) NOT NULL,                    -- 회원 ID (외래키)
  IMAGE             BLOB,                                   -- 프로필 이미지
  INTRO             VARCHAR2(500),                          -- 자기소개
  NICKNAME          VARCHAR2(30),                           -- 닉네임 (UNIQUE)
  SALES_COUNT       NUMBER(10) DEFAULT 0,                   -- 누적 판매 건수
  TOTAL_SALES       NUMBER(10) DEFAULT 0,                   -- 누적 매출액
  REVIEW_AVG        NUMBER(3,2) DEFAULT 0,                  -- 평균 평점
  REVIEW_COUNT      NUMBER(10) DEFAULT 0,                   -- 리뷰 수
  RECENT_ORDER_CNT  NUMBER(10) DEFAULT 0,                   -- 최근 1주 주문 수
  RECENT_QNA_CNT    NUMBER(10) DEFAULT 0,                   -- 최근 1주 문의 수
  FOLLOWER_COUNT    NUMBER(10) DEFAULT 0,                   -- 팔로워 수
  PRODUCT_COUNT     NUMBER(10) DEFAULT 0,                   -- 등록 상품 수
  CATEGORY          VARCHAR2(50),                           -- 주력 카테고리
  BANK_ACCOUNT      VARCHAR2(50),                           -- 정산 계좌
  SNS_LINK          VARCHAR2(200),                          -- SNS 링크
  IS_ACTIVE         CHAR(1) DEFAULT 'Y',                    -- 활동 여부
  CREATE_DATE       TIMESTAMP DEFAULT systimestamp,         -- 생성일시
  UPDATE_DATE       TIMESTAMP DEFAULT systimestamp          -- 수정일시
);


-- =======================================
-- ✅ 3. 제약 조건 추가
-- =======================================

-- 닉네임 중복 방지 (UNIQUE)
ALTER TABLE SELLER_PAGE ADD CONSTRAINT UK_SELLERPAGE_NICKNAME UNIQUE (NICKNAME);

-- 회원 외래키 참조 (MEMBER 테이블)
ALTER TABLE SELLER_PAGE ADD CONSTRAINT FK_SELLER_PAGE_MEMBER_ID FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(MEMBER_ID);



-- =======================================
-- ✅ 4. 시퀀스 생성
-- =======================================
CREATE SEQUENCE SELLER_PAGE_SEQ
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

-- =======================================
-- ✅ 5. 샘플 데이터 삽입
-- =======================================
INSERT INTO SELLER_PAGE (
  PAGE_ID, MEMBER_ID, IMAGE, INTRO, NICKNAME,
  SALES_COUNT, TOTAL_SALES, REVIEW_AVG, REVIEW_COUNT,
  RECENT_ORDER_CNT, RECENT_QNA_CNT, FOLLOWER_COUNT, PRODUCT_COUNT,
  CATEGORY, BANK_ACCOUNT, SNS_LINK, IS_ACTIVE,
  CREATE_DATE, UPDATE_DATE
) VALUES (
  SELLER_PAGE_SEQ.NEXTVAL, 2, NULL, '안녕하세요. 여행 가이드 신형만입니다.', '신형만',
  25, 980000, 4.25, 25,
  9, 2, 250, 3,
  '로컬 체험', '신한 123-456-7890', 'https://instagram.com/sin_guide', 'Y',
  DEFAULT, DEFAULT
);


-- 전화번호, 우편번호, 주소, 상세주소 추가
ALTER TABLE SELLER_PAGE ADD TEL VARCHAR2(20);             -- 전화번호
ALTER TABLE SELLER_PAGE ADD ZONECODE VARCHAR2(10);        -- 우편번호
ALTER TABLE SELLER_PAGE ADD ADDRESS VARCHAR2(200);        -- 기본 주소
ALTER TABLE SELLER_PAGE ADD DETAIL_ADDRESS VARCHAR2(200); -- 상세 주소

COMMIT;

