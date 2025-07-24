-- 기존 테이블 및 시퀀스 삭제 (있으면)
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE member_terms CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE terms CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE member_role CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE role CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE member CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE member_member_id_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;


BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE terms_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;


-- 역할 테이블
CREATE TABLE role (
  role_id    VARCHAR2(11) PRIMARY KEY,
  role_name  VARCHAR2(50) NOT NULL
);

-- 회원 테이블 (생년월일 추가됨)
CREATE TABLE member (
  member_id       NUMBER(10),
  email           VARCHAR2(40) NOT NULL,
  name            VARCHAR2(50) NOT NULL,
  passwd          VARCHAR2(100) NOT NULL,
  tel             VARCHAR2(13),
  nickname        VARCHAR2(30),
  gender          VARCHAR2(6),
  address         VARCHAR2(200),
  birth_date      DATE,  -- ✅ 생년월일
  pic             BLOB,
  create_date     TIMESTAMP DEFAULT SYSTIMESTAMP,
  update_date     TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 제약조건 추가
ALTER TABLE member ADD CONSTRAINT member_member_id_pk PRIMARY KEY (member_id);
ALTER TABLE member ADD CONSTRAINT member_email_uk UNIQUE(email);
ALTER TABLE member ADD CONSTRAINT member_gender_ck CHECK (gender IN ('남자','여자'));
ALTER TABLE member ADD CONSTRAINT member_nickname_uk UNIQUE(nickname);

-- 시퀀스 생성
CREATE SEQUENCE member_member_id_seq;

-- 샘플 데이터 삽입
INSERT INTO role (role_id, role_name) VALUES ('R01', '구매자');
INSERT INTO role (role_id, role_name) VALUES ('R02', '판매자');

-- 회원 샘플 데이터 (생년월일 추가)
INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date)
VALUES (member_member_id_seq.NEXTVAL, 'shinnosuke@naver.com', '신짱구', '1234', '010-1111-1111', '짱구', '남자', '떡잎마을', TO_DATE('2010-03-01','YYYY-MM-DD'));

INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date)
VALUES (member_member_id_seq.NEXTVAL, 'hiroshi@naver.com', '신형만', '5678', '010-2222-2222', '형만이', '남자', '떡잎마을', TO_DATE('1980-11-03','YYYY-MM-DD'));

INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date)
VALUES (member_member_id_seq.NEXTVAL, 'misae@naver.com', '봉미선', 'abcd', '010-3333-3333', '미선이', '여자', '떡잎마을', TO_DATE('1982-06-15','YYYY-MM-DD'));

INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date)
VALUES (member_member_id_seq.NEXTVAL, 'nene@naver.com', '짱아', 'efgh', '010-4444-4444', '짱아', '여자', '떡잎마을', TO_DATE('2015-09-09','YYYY-MM-DD'));

INSERT INTO member (member_id, email, name, passwd, tel, nickname, gender, address, birth_date)
VALUES (member_member_id_seq.NEXTVAL, 'bo@naver.com', '맹구', 'ijkl', '010-5555-5555', '맹구', '남자', '떡잎마을', TO_DATE('2010-07-24','YYYY-MM-DD'));

-- 역할 매핑
CREATE TABLE member_role (
  member_id  NUMBER(10),
  role_id    VARCHAR2(11),
  PRIMARY KEY (member_id, role_id),
  FOREIGN KEY (member_id) REFERENCES member(member_id),
  FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- 초기 역할 지정
INSERT INTO member_role VALUES (1, 'R01'); -- 짱구 : 구매자
INSERT INTO member_role VALUES (2, 'R02'); -- 형만 : 판매자
INSERT INTO member_role VALUES (3, 'R01'); -- 봉미선 : 구매자
INSERT INTO member_role VALUES (4, 'R01'); -- 짱아 : 구매자
INSERT INTO member_role VALUES (5, 'R02'); -- 맹구 : 판매자

-- 역할 전환/제거 예시
INSERT INTO member_role VALUES (1, 'R02');
DELETE FROM member_role WHERE member_id = 1 AND role_id = 'R01';

-- 커밋
COMMIT;

-- 약관 테이블 및 트리거
CREATE SEQUENCE terms_seq;

CREATE TABLE terms (
  terms_id     NUMBER(10) PRIMARY KEY,
  name         VARCHAR2(50) NOT NULL,
  content      CLOB NOT NULL,
  is_required  CHAR(1) DEFAULT 'Y' CHECK (is_required IN ('Y','N')),
  version      VARCHAR2(20) NOT NULL,
  created_at   TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);

CREATE OR REPLACE TRIGGER trg_terms_before_insert
BEFORE INSERT ON terms
FOR EACH ROW
BEGIN
  IF :NEW.terms_id IS NULL THEN
    SELECT terms_seq.NEXTVAL INTO :NEW.terms_id FROM dual;
  END IF;
END;


-- 회원 약관 동의 테이블
CREATE TABLE member_terms (
  member_id  NUMBER(10) NOT NULL,
  terms_id   NUMBER(10) NOT NULL,
  agreed_at  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
  PRIMARY KEY (member_id, terms_id),
  FOREIGN KEY (member_id) REFERENCES member(member_id),
  FOREIGN KEY (terms_id) REFERENCES terms(terms_id)
);
