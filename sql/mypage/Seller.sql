CREATE TABLE SELLER_PAGE (
    PAGE_ID      NUMBER(10) PRIMARY KEY,                         -- 마이페이지 ID (시퀀스 사용 가능)
    MEMBER_ID    NUMBER(10) NOT NULL,                            -- 회원 아이디 (FK)
    IMAGE        BLOB,                                           -- 프로필 이미지
    INTRO        VARCHAR2(500),                                  -- 자기소개글
    SALES_COUNT  NUMBER(10) DEFAULT 0,                           -- 누적 판매 건수
    REVIEW_AVG   NUMBER(3,2) DEFAULT 0,                          -- 평균 평점 (예: 4.25)
    CREATE_DATE  TIMESTAMP DEFAULT systimestamp,                -- 생성일시
    UPDATE_DATE  TIMESTAMP DEFAULT systimestamp,                -- 수정일시
    CONSTRAINT FK_SELLER_PAGE_MEMBER_ID FOREIGN KEY (MEMBER_ID)
        REFERENCES MEMBER(MEMBER_ID)
);

CREATE SEQUENCE SELLER_PAGE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

INSERT INTO SELLER_PAGE (PAGE_ID, MEMBER_ID, IMAGE, INTRO, SALES_COUNT, REVIEW_AVG, CREATE_DATE, UPDATE_DATE)
            VALUES (SELLER_PAGE_SEQ.NEXTVAL, 2, NULL, '안녕하세요. 여행 가이드 신형만입니다.', 25, 4.25, DEFAULT, DEFAULT);
