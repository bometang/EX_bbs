-- 기존 테이블/시퀀스 삭제 (있으면)
begin
    execute immediate 'drop table member_role cascade constraints';
exception when others then null;
end;
/

begin
    execute immediate 'drop table role cascade constraints';
exception when others then null;
end;
/

begin
    execute immediate 'drop table member cascade constraints';
exception when others then null;
end;
/

begin
    execute immediate 'drop sequence member_member_id_seq';
exception when others then null;
end;
/

-- role 테이블 생성 (구매자, 판매자)
create table role (
    role_id    varchar2(11) primary key,  -- 역할값 (R01, R02)
    role_name  varchar2(50) not null      -- 역할명 (구매자, 판매자)
);

-- role 데이터 삽입
insert into role (role_id, role_name) values ('R01', '구매자');
insert into role (role_id, role_name) values ('R02', '판매자');

-- 회원 테이블 생성 (member_type 제거)
create table member (
    member_id       number(10),               -- 내부 관리 아이디
    email           varchar2(40) not null,    -- 로그인 아이디
    name            varchar2(50) not null,    -- 회원 이름
    passwd          varchar2(12) not null,    -- 로그인 비밀번호
    tel             varchar2(13),             -- 연락처 ex)010-1234-5678
    nickname        varchar2(30),             -- 별칭
    gender          varchar2(6),              -- 성별
    address         varchar2(200),            -- 주소
    pic             blob,                     -- 사진
    create_date     timestamp default systimestamp, -- 생성일시
    update_date     timestamp default systimestamp  -- 수정일시
);

-- 제약조건 추가
alter table member
    add constraint member_member_id_pk
    primary key (member_id);

alter table member
    add constraint member_email_uk
    unique(email);

alter table member
    add constraint member_gender_ck
    check (gender in ('남자','여자'));

-- 시퀀스 생성
create sequence member_member_id_seq;

-- 회원 샘플 데이터 삽입
insert into member (member_id, email, name, passwd, tel, nickname, gender, address)
values (member_member_id_seq.nextval, 'shinnosuke@naver.com', '신짱구', '1234', '010-1111-1111', '짱구', '남자', '떡잎마을');

insert into member (member_id, email, name, passwd, tel, nickname, gender, address)
values (member_member_id_seq.nextval, 'hiroshi@naver.com', '신형만', '5678', '010-2222-2222', '형만이', '남자', '떡잎마을');

insert into member (member_id, email, name, passwd, tel, nickname, gender, address)
values (member_member_id_seq.nextval, 'misae@naver.com', '봉미선', 'abcd', '010-3333-3333', '미선이', '여자', '떡잎마을');

insert into member (member_id, email, name, passwd, tel, nickname, gender, address)
values (member_member_id_seq.nextval, 'nene@naver.com', '짱아', 'efgh', '010-4444-4444', '짱아', '여자', '떡잎마을');

insert into member (member_id, email, name, passwd, tel, nickname, gender, address)
values (member_member_id_seq.nextval, 'bo@naver.com', '맹구', 'ijkl', '010-5555-5555', '맹구', '남자', '떡잎마을');

-- member_role 매핑 테이블 생성
create table member_role (
    member_id  number(10),          -- 회원 ID
    role_id    varchar2(11),        -- 역할 ID
    primary key (member_id, role_id),
    foreign key (member_id) references member(member_id),
    foreign key (role_id) references role(role_id)
);

-- 초기 역할 할당 (짱구, 봉미선, 짱아는 구매자 / 형만, 맹구는 판매자)
insert into member_role values (1, 'R01'); -- 짱구 : 구매자
insert into member_role values (2, 'R02'); -- 형만 : 판매자
insert into member_role values (3, 'R01'); -- 봉미선 : 구매자
insert into member_role values (4, 'R01'); -- 짱아 : 구매자
insert into member_role values (5, 'R02'); -- 맹구 : 판매자

-- 커밋
commit;

-- 역할 전환 : 짱구를 판매자로 등록 (버튼 누른 효과)
insert into member_role values (1, 'R02');

-- 역할 제거 : 짱구를 구매자 역할에서 제거
delete from member_role where member_id = 1 and role_id = 'R01';

-- 커밋
commit;

-- 회원별 역할 조회
select m.name, r.role_name
from member m
join member_role mr on m.member_id = mr.member_id
join role r on mr.role_id = r.role_id
order by m.member_id;


--------------------------약 관 동 의-----------------------------

-- 1. 시퀀스 생성 (terms_id 자동 증가용)
CREATE SEQUENCE terms_seq;

-- 2. terms 테이블 생성
CREATE TABLE terms (
  terms_id    NUMBER(10) PRIMARY KEY,            -- 약관 고유 ID (자동증가)
  name        VARCHAR2(50) NOT NULL,             -- 약관 이름
  content     CLOB NOT NULL,                      -- 약관 전문 (내용)
  is_required CHAR(1) DEFAULT 'Y' CHECK (is_required IN ('Y','N')),  -- 필수 여부
  version     VARCHAR2(20) NOT NULL,              -- 약관 버전
  created_at  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL  -- 등록일
);

-- 3. 트리거 생성 (insert 시 자동으로 terms_id 채움)
CREATE OR REPLACE TRIGGER trg_terms_before_insert
BEFORE INSERT ON terms
FOR EACH ROW
BEGIN
  IF :NEW.terms_id IS NULL THEN
    SELECT terms_seq.NEXTVAL INTO :NEW.terms_id FROM dual;
  END IF;
END;
/

-- 4. member_terms 테이블 생성 (회원이 동의한 약관 기록)
CREATE TABLE member_terms (
  member_id NUMBER(10) NOT NULL,       -- 동의한 멤버 ID (외래키)
  terms_id  NUMBER(10) NOT NULL,       -- 동의한 약관 ID (외래키)
  agreed_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,  -- 동의한 시각
  PRIMARY KEY (member_id, terms_id),
  FOREIGN KEY (member_id) REFERENCES member(member_id),
  FOREIGN KEY (terms_id) REFERENCES terms(terms_id)
);








