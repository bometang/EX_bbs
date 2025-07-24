-- 기존 테이블 삭제 (예외 무시)
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE buyer_page CASCADE CONSTRAINTS';
EXCEPTION
  WHEN OTHERS THEN NULL;
END;
/

-- 기존 시퀀스 삭제
DROP SEQUENCE buyer_page_seq;

-- 테이블 재생성
CREATE TABLE BUYER_PAGE (
    PAGE_ID          NUMBER(10)       PRIMARY KEY,                           -- 마이페이지 ID
    MEMBER_ID        NUMBER(10)       NOT NULL,                              -- 회원 ID (외래키)
    IMAGE            BLOB,                                                  -- 프로필 이미지
    INTRO            VARCHAR2(500),                                         -- 자기소개글
    RECENT_ORDER     VARCHAR2(100),                                         -- 최근 주문 상품명
    POINT            NUMBER(10),                                            -- 적립 포인트
    TEL              VARCHAR2(20),                                          -- 연락처
    ADDRESS          VARCHAR2(200),                                         -- 주소
    ZONECODE         VARCHAR2(10),                                          -- 우편번호
    DETAIL_ADDRESS   VARCHAR2(200),                                         -- 상세주소
    NOTIFICATION     VARCHAR2(1),                                           -- 알림 수신 여부 ('Y' or NULL)
    NICKNAME         VARCHAR2(30),                                          -- 닉네임
    CREATE_DATE      TIMESTAMP         DEFAULT systimestamp,                -- 생성일시
    UPDATE_DATE      TIMESTAMP         DEFAULT systimestamp,                -- 수정일시
    CONSTRAINT FK_BUYER_PAGE_MEMBER_ID FOREIGN KEY (MEMBER_ID)
        REFERENCES MEMBER(MEMBER_ID)
);


-- 시퀀스 재생성
CREATE SEQUENCE BUYER_PAGE_SEQ
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

-- 닉네임 유니크 제약조건 추가
ALTER TABLE BUYER_PAGE
  ADD CONSTRAINT UK_BUYERPAGE_NICKNAME UNIQUE (NICKNAME);

-- 샘플 데이터 삽입
INSERT INTO BUYER_PAGE (PAGE_ID, MEMBER_ID, IMAGE, INTRO, RECENT_ORDER, POINT, TEL, ADDRESS, ZONECODE, DETAIL_ADDRESS, NOTIFICATION, NICKNAME, CREATE_DATE, UPDATE_DATE)
VALUES (BUYER_PAGE_SEQ.NEXTVAL, 1, NULL, '안녕하세요. 여행을 좋아하는 사람입니다.', '제주도 해안도로 투어', 3000, '010-1234-5678', '서울시 강남구 테헤란로', '06130', '101동 202호', 'Y', 'user1', DEFAULT, DEFAULT);

COMMIT;
