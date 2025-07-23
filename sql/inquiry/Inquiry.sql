SELECT * FROM inquiry;


-- =============================
-- INQUIRY 테이블 삭제 (존재할 때만 삭제)
-- =============================

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE INQUIRY CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- =============================
-- INQUIRY 시퀀스 삭제 (존재할 때만 삭제)
-- =============================

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_INQUIRY_ID';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/


-- =============================
-- INQUIRY 테이블 생성
-- =============================
CREATE TABLE INQUIRY (
    INQUIRY_ID      NUMBER(20)         CONSTRAINT PK_INQUIRY PRIMARY KEY,      -- 문의 고유번호
    MEMBER_ID       NUMBER(20)         CONSTRAINT FK_INQUIRY_MEMBER REFERENCES MEMBER(MEMBER_ID),  -- 회원 ID (FK)
    TITLE           VARCHAR2(200 CHAR) NOT NULL,                                -- 제목
    CONTENT         CLOB,                                                      -- 내용
    ATTACH_FILE     VARCHAR2(300 CHAR),                                         -- 첨부파일 경로
    INQUIRY_DATE    DATE            DEFAULT SYSDATE NOT NULL,                  -- 작성일
    ANSWER_CONTENT  CLOB,                                                      -- 답변 내용
    ANSWER_DATE     DATE,                                                      -- 답변 작성일
    STATUS          VARCHAR2(20 CHAR) CONSTRAINT CK_INQUIRY_STATUS CHECK (STATUS IN ('미답변', '답변완료')) -- 상태코드 (예시)
);

-- =============================
-- COMMENT 추가 (가독성 & 유지보수 위해)
-- =============================
COMMENT ON TABLE INQUIRY IS '1:1 문의 테이블';
COMMENT ON COLUMN INQUIRY.INQUIRY_ID IS '문의 고유번호';
COMMENT ON COLUMN INQUIRY.MEMBER_ID IS '작성자 회원번호 (FK)';
COMMENT ON COLUMN INQUIRY.TITLE IS '문의 제목';
COMMENT ON COLUMN INQUIRY.CONTENT IS '문의 내용';
COMMENT ON COLUMN INQUIRY.ATTACH_FILE IS '첨부파일 경로';
COMMENT ON COLUMN INQUIRY.INQUIRY_DATE IS '문의 작성일';
COMMENT ON COLUMN INQUIRY.ANSWER_CONTENT IS '답변 내용';
COMMENT ON COLUMN INQUIRY.ANSWER_DATE IS '답변 작성일';
COMMENT ON COLUMN INQUIRY.STATUS IS '답변 상태';

-- =============================
-- SEQUENCE 생성 (PK 시퀀스)
-- =============================
CREATE SEQUENCE SEQ_INQUIRY_ID
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;


-- =============================
-- INQUIRY 테이블 샘플 데이터 삽입
-- =============================

-- 1번 회원이 문의 등록 (미답변)
INSERT INTO INQUIRY (
    INQUIRY_ID,
    MEMBER_ID,
    TITLE,
    CONTENT,
    ATTACH_FILE,
    INQUIRY_DATE,
    STATUS
) VALUES (
    SEQ_INQUIRY_ID.NEXTVAL,
    1,  -- 짱구
    '결제 오류 문의',
    '결제 시 오류가 발생했습니다. 확인 부탁드립니다.',
    '/upload/inquiry/20240613_error.png',
    SYSDATE,
    '미답변'
);

-- 2번 회원이 문의 등록 (답변완료)
INSERT INTO INQUIRY (
    INQUIRY_ID,
    MEMBER_ID,
    TITLE,
    CONTENT,
    ATTACH_FILE,
    INQUIRY_DATE,
    ANSWER_CONTENT,
    ANSWER_DATE,
    STATUS
) VALUES (
    SEQ_INQUIRY_ID.NEXTVAL,
    2,  -- 신형만
    '상품 등록 관련 문의',
    '신규 상품 등록이 안됩니다.',
    NULL,
    SYSDATE - 3,
    '시스템 점검이 완료되어 현재 정상 등록 가능합니다.',
    SYSDATE - 1,
    '답변완료'
);

-- 3번 회원이 문의 등록 (미답변)
INSERT INTO INQUIRY (
    INQUIRY_ID,
    MEMBER_ID,
    TITLE,
    CONTENT,
    ATTACH_FILE,
    INQUIRY_DATE,
    STATUS
) VALUES (
    SEQ_INQUIRY_ID.NEXTVAL,
    3,  -- 봉미선
    '배송 지연 문의',
    '주문한 상품이 아직 도착하지 않았습니다.',
    NULL,
    SYSDATE - 5,
    '미답변'
);

-- 4번 회원이 문의 등록 (답변완료)
INSERT INTO INQUIRY (
    INQUIRY_ID,
    MEMBER_ID,
    TITLE,
    CONTENT,
    ATTACH_FILE,
    INQUIRY_DATE,
    ANSWER_CONTENT,
    ANSWER_DATE,
    STATUS
) VALUES (
    SEQ_INQUIRY_ID.NEXTVAL,
    4,  -- 짱아
    '회원 탈퇴 방법',
    '회원 탈퇴는 어떻게 하나요?',
    NULL,
    SYSDATE - 7,
    '회원탈퇴는 마이페이지 > 회원정보수정에서 가능합니다.',
    SYSDATE - 6,
    '답변완료'
);

