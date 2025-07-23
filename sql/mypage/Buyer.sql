-- 테이블 생성
CREATE TABLE BUYER_PAGE (
    PAGE_ID        NUMBER(10)       PRIMARY KEY,                           -- 마이페이지 ID
    MEMBER_ID      NUMBER(10)       NOT NULL,                              -- 회원 ID
    IMAGE          BLOB,                                                  -- 프로필 이미지
    INTRO          VARCHAR2(500),                                         -- 자기소개글
    RECENT_ORDER   VARCHAR2(100),                                         -- 최근 주문 상품명
    POINT          NUMBER(10),                                            -- 적립 포인트
    CREATE_DATE    TIMESTAMP         DEFAULT systimestamp,                -- 생성일시
    UPDATE_DATE    TIMESTAMP         DEFAULT systimestamp,                -- 수정일시
    CONSTRAINT FK_BUYER_PAGE_MEMBER_ID FOREIGN KEY (MEMBER_ID)
        REFERENCES MEMBER(MEMBER_ID)                                      -- 외래키
);

-- 회원 목록 확인
SELECT member_id, name FROM member;


-- 시퀀스 생성
CREATE SEQUENCE BUYER_PAGE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 샘플 INSERT
INSERT INTO BUYER_PAGE (PAGE_ID, MEMBER_ID, IMAGE, INTRO, RECENT_ORDER, POINT, CREATE_DATE, UPDATE_DATE)
VALUES (BUYER_PAGE_SEQ.NEXTVAL, 1, NULL, '안녕하세요. 여행을 좋아하는 사람입니다.', '제주도 해안도로 투어', 3000, DEFAULT, DEFAULT);


