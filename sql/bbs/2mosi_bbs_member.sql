INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'a', '김민준',   'a', '010-1234-0001', 'minjun',   '남자', '서울시 강남구 삼성동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'ㅁ', '이서현',   'ㅁ', '010-1234-0002', 'seohyun',  '여자', '서울시 마포구 상수동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test3@kh.com', '박지훈',   'qwer1234!', '010-1234-0003', 'jhoon',    '남자', '부산시 해운대구 우동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test4@kh.com', '최유나',   'qwer1234!', '010-1234-0004', 'yuna',     '여자', '대구시 중구 동덕로');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test5@kh.com', '정우성',   'qwer1234!', '010-1234-0005', 'woosung',  '남자', '인천시 연수구 송도동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test6@kh.com', '한지민',   'qwer1234!', '010-1234-0006', 'jimin',    '여자', '광주광역시 남구 봉선동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test7@kh.com', '송중기',   'qwer1234!', '010-1234-0007', 'joongki',  '남자', '대전시 서구 둔산동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test8@kh.com', '윤아린',   'qwer1234!', '010-1234-0008', 'arin',     '여자', '울산시 남구 삼산동');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test9@kh.com', '강동원',   'qwer1234!', '010-1234-0009', 'dongwon',  '남자', '경기도 성남시 분당구');

INSERT INTO MEMBER (MEMBER_ID, EMAIL, NAME, PASSWD, TEL, NICKNAME, GENDER, ADDRESS)
VALUES (member_member_id_seq.NEXTVAL, 'test10@kh.com','장예원', 'qwer1234!', '010-1234-0010', 'yewon',    '여자', '강원도 춘천시 후평동');

----------------------------------------------------------------------
--코드
----------------------------------------------------------------------
INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B01','카테고리',NULL);

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0101','장애인','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0102','의료/미용','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0103','시즌','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0104','반려견','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0105','문화','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0106','맛집','B01');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0107','실버','B01');
----------------------------------------------------------------------
INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B02','게시판 상태',NULL);

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0201','일반','B02');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0202','삭제','B02');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('B0203','임시저장','B02');
----------------------------------------------------------------------
INSERT INTO code(code_id,decode,pcode_id)
VALUES ('R02','댓글 상태',NULL);

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('R0201','일반','R02');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('R0202','삭제','R02');

INSERT INTO code(code_id,decode,pcode_id)
VALUES ('R0203','임시저장','R02');
