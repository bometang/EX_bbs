--테이블 삭제
DROP TABLE rbbs_like;
DROP TABLE rbbs_report;
DROP TABLE bbs_upload;
drop table rbbs;
drop table bbs_like;
DROP TABLE bbs_report;
drop table bbs;
--DROP TABLE code;

--DROP TABLE member_role;
--DROP TABLE ROLE;
--DROP TABLE MEMBER;
--DROP SEQUENCE member_member_id_seq;

--DROP TABLE terms;
--DROP TABLE MEMBER_TERMS;
--DROP SEQUENCE terms_seq;
--시퀀스삭제
drop sequence bbs_bbs_id_seq;
DROP SEQUENCE bbs_upload_upload_id_seq;
DROP SEQUENCE rbbs_rbbs_id_seq;
DROP SEQUENCE bbs_upload_upload_group_seq;

 -- MEMBER 테이블
--CREATE TABLE member (
--  member_id       NUMBER(10),
--  email           VARCHAR2(40) NOT NULL,
--  name            VARCHAR2(50) NOT NULL,
--  passwd          VARCHAR2(12) NOT NULL,
--  tel             VARCHAR2(13),
--  nickname        VARCHAR2(30),
--  gender          VARCHAR2(6),
--  address         VARCHAR2(200),
--  birth_date      DATE,  -- ✅ 생년월일
--  pic             BLOB,
--  create_date     TIMESTAMP DEFAULT SYSTIMESTAMP,
--  update_date     TIMESTAMP DEFAULT SYSTIMESTAMP
--);
---- 제약조건 추가
--ALTER TABLE member ADD CONSTRAINT member_member_id_pk PRIMARY KEY (member_id);
--ALTER TABLE member ADD CONSTRAINT member_email_uk UNIQUE(email);
--ALTER TABLE member ADD CONSTRAINT member_gender_ck CHECK (gender IN ('남자','여자'));
--
---- 시퀀스 생성
--CREATE SEQUENCE member_member_id_seq;
--
---- ROLE 테이블
--CREATE TABLE ROLE (
--  ROLE_ID    VARCHAR2(11)  NOT NULL,
--  ROLE_NAME  VARCHAR2(50)  NOT NULL,
--  CONSTRAINT PK_ROLE       PRIMARY KEY (ROLE_ID)
--);
--
--
---- MEMBER_ROLE 테이블 (회원·역할 매핑)
--CREATE TABLE MEMBER_ROLE (
--  MEMBER_ID  NUMBER(10)    NOT NULL,
--  ROLE_ID    VARCHAR2(11)  NOT NULL,
--  CONSTRAINT PK_MEMBER_ROLE           PRIMARY KEY (MEMBER_ID, ROLE_ID),
--  CONSTRAINT FK_MR_ROLE               FOREIGN KEY (ROLE_ID)   REFERENCES ROLE   (ROLE_ID)
--);
--
--ALTER TABLE MEMBER_ROLE
--ADD CONSTRAINT FK_MR_MEMBER
--  FOREIGN KEY (MEMBER_ID)
--  REFERENCES MEMBER (MEMBER_ID)
--  ON DELETE CASCADE;

--------------------------------------------------------
--코드
--------------------------------------------------------
--create table code(
--code_id     varchar2(11) PRIMARY KEY,       --코드
--DECODE      varchar2(30) NOT null,          --코드명
--discript    clob,                           --코드설명
--pcode_id    varchar2(11),                   --상위코드
--useyn       char(1) default 'Y' NOT null,   --사용여부 (사용:'Y',미사용:'N')
--cdate       timestamp default systimestamp,
--udate       timestamp
--);
----외래키
--alter table code
--add constraint fk_code_pcode_id
--foreign key(pcode_id)
--references code(code_id);
--
----제약조건
--alter table code add constraint code_useyn_ck check(useyn in ('Y','N'));
--------------------------------------------------------

--------------------------------------------------------
--게시판
--------------------------------------------------------
create table bbs(
BBS_ID       number(10)    PRIMARY KEY,
BCATEGORY    varchar2(11)  NOT null,
status       varchar2(11)  NOT NULL,
title        varchar2(100) NOT null,
member_id    number(10)    NOT null,
hit          number(5)     DEFAULT 0 NOT null,
bcontent     clob          NOT null,
pbbs_id      number(10),
bgroup       number(10)		NOT null,
step         number(3)		NOT null,
bindent      number(3)		NOT null,
CREATE_DATE  timestamp    default systimestamp,
UPDATE_DATE  timestamp    default systimestamp
);
-- 작성자 아이디 외래키 지정
ALTER TABLE bbs
ADD CONSTRAINT fk_bbs_md
FOREIGN KEY (member_id)
REFERENCES member(member_id);

-- 상태코드 외래키 지정
ALTER TABLE bbs
ADD CONSTRAINT fk_bbs_BC
FOREIGN KEY (BCATEGORY)
REFERENCES code(code_id);
--

-- 상태코드 외래키 지정
ALTER TABLE bbs
ADD CONSTRAINT fk_bbs_status
FOREIGN KEY (status)
REFERENCES code(code_id);
--

-- 상태코드 외래키 지정
ALTER TABLE bbs
ADD CONSTRAINT fk_bbs_PD
FOREIGN KEY (PBBS_ID)
REFERENCES bbs(BBS_ID);
--

--시퀸스 생성
CREATE SEQUENCE bbs_bbs_id_seq;
--------------------------------------------------------


--------------------------------------------------------
--게시판 좋아요 테이블
--------------------------------------------------------
CREATE TABLE bbs_like (
bbs_id       NUMBER(10)    NOT NULL,
member_id    NUMBER(10)    NOT NULL,
create_date  TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
CONSTRAINT pk_bbs_like       PRIMARY KEY (bbs_id, member_id)
);

-- 게시글 아이디 외래키 지정
ALTER TABLE bbs_like
ADD CONSTRAINT fk_bbs_like_bbs
FOREIGN KEY (bbs_id)
REFERENCES bbs(BBS_ID);
--

-- 작성자 아이디 외래키 지정
ALTER TABLE bbs_like
ADD CONSTRAINT fk_bbs_like_mem
FOREIGN KEY (member_id)
REFERENCES member(member_id);
--------------------------------------------------------

--------------------------------------------------------
-- 게시판 신고 테이블
--------------------------------------------------------
CREATE TABLE bbs_report (
bbs_id      NUMBER(10)    NOT NULL,
member_id   NUMBER(10)    NOT NULL,
reason      VARCHAR2(300) NULL,
report_date TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
CONSTRAINT pk_bbs_report       PRIMARY KEY (bbs_id, member_id)
);

-- 게시글 아이디 외래키 지정
ALTER TABLE bbs_report
ADD CONSTRAINT fk_bbs_report_bbs
FOREIGN KEY (bbs_id)
REFERENCES bbs(BBS_ID);
--

-- 작성자 아이디 외래키 지정
ALTER TABLE bbs_report
ADD CONSTRAINT fk_bbs_report_mem
FOREIGN KEY (member_id)
REFERENCES member(member_id);
--------------------------------------------------------

--------------------------------------------------------
-- 댓글 테이블 생성
--------------------------------------------------------
CREATE TABLE rbbs (
  rbbs_id      NUMBER(10)     PRIMARY KEY,            -- 댓글 PK
  bbs_id       NUMBER(10)     NOT NULL,               -- 원글 ID
  member_id    NUMBER(10)     NOT NULL,               -- 작성자 회원 ID
  status       VARCHAR2(11),                            -- 댓글 상태 코드
  prbbs_id     NUMBER(10),
  bcontent     CLOB           NOT NULL,               -- 댓글 내용
  bgroup       NUMBER(10),                              -- 부모 댓글 ID (self reference)
  step         NUMBER(3)      NOT NULL,               -- 출력 순서
  bindent      NUMBER(3)      NOT NULL,               -- 들여쓰기 깊이
  create_date  TIMESTAMP      DEFAULT SYSTIMESTAMP,   -- 생성 일시
  update_date  TIMESTAMP      DEFAULT SYSTIMESTAMP    -- 수정 일시
);

-- 외래키: 원글 참조
ALTER TABLE rbbs
  ADD CONSTRAINT fk_rbbs_bbs
    FOREIGN KEY (bbs_id)
    REFERENCES bbs(bbs_id);

-- 외래키: 작성자(회원) 참조
ALTER TABLE rbbs
  ADD CONSTRAINT fk_rbbs_member
    FOREIGN KEY (member_id)
    REFERENCES member(member_id);

-- 외래키: 부모 댓글(self reference)
ALTER TABLE rbbs
  ADD CONSTRAINT fk_rbbs_parent
    FOREIGN KEY (bgroup)
    REFERENCES rbbs(rbbs_id);

-- 외래키: 상태코드 참조
ALTER TABLE rbbs
  ADD CONSTRAINT fk_rbbs_status
    FOREIGN KEY (status)
    REFERENCES code(code_id);

-- 시퀀스 생성
CREATE SEQUENCE rbbs_rbbs_id_seq;
--------------------------------------------------------

--------------------------------------------------------
--댓글 좋아요 테이블
--------------------------------------------------------
CREATE TABLE rbbs_like (
rbbs_id      NUMBER(10)    NOT NULL,
member_id    NUMBER(10)    NOT NULL,
create_date  TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
CONSTRAINT rpk_bbs_like       PRIMARY KEY (rbbs_id, member_id)
);

-- 게시글 아이디 외래키 지정
ALTER TABLE rbbs_like
ADD CONSTRAINT fk_rbbs_like_bbs
FOREIGN KEY (rbbs_id)
REFERENCES rbbs(RBBS_ID);
--

-- 작성자 아이디 외래키 지정
ALTER TABLE rbbs_like
ADD CONSTRAINT fk_rbbs_like_mem
FOREIGN KEY (member_id)
REFERENCES member(member_id);
--------------------------------------------------------

--------------------------------------------------------
-- 댓글 신고 테이블
--------------------------------------------------------
CREATE TABLE rbbs_report (
rbbs_id     NUMBER(10)    NOT NULL,
member_id   NUMBER(10)    NOT NULL,
reason      VARCHAR2(300) NULL,
report_date TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
CONSTRAINT pk_rbbs_report       PRIMARY KEY (rbbs_id, member_id)
);

-- 게시글 아이디 외래키 지정
ALTER TABLE rbbs_report
ADD CONSTRAINT fk_rbbs_report_bbs
FOREIGN KEY (rbbs_id)
REFERENCES rbbs(RBBS_ID);
--

-- 작성자 아이디 외래키 지정
ALTER TABLE rbbs_report
ADD CONSTRAINT fk_rbbs_report_mem
FOREIGN KEY (member_id)
REFERENCES member(member_id);
--------------------------------------------------------

--------------------------------------------------------
-- 게시글 사진 테이블 생성
--------------------------------------------------------
CREATE TABLE bbs_upload (
  upload_id    NUMBER(10)       PRIMARY KEY,
  bbs_id      NUMBER(10),
  upload_group NUMBER(10),
  file_type VARCHAR2(20) DEFAULT 'INLINE',
  sort_order  NUMBER(5)        NOT NULL,      -- 본문 내 삽입 순서
  file_path   VARCHAR2(2000)   NOT NULL,
  original_name VARCHAR2(255),
  saved_name    VARCHAR2(255),
  uploaded_at TIMESTAMP        DEFAULT SYSTIMESTAMP
);

ALTER TABLE bbs_upload
  ADD CONSTRAINT fk_bbs_upload_bbs
    FOREIGN KEY (bbs_id)
    REFERENCES bbs(bbs_id)
    ON DELETE SET NULL;

-- 시퀀스 생성
CREATE SEQUENCE bbs_upload_upload_id_seq;
CREATE SEQUENCE bbs_upload_upload_group_seq;

