-- 기존 테이블 삭제
DROP TABLE product CASCADE CONSTRAINT;
DROP TABLE product_image CASCADE CONSTRAINT;
DROP TABLE product_course_point CASCADE CONSTRAINT;

-- 기존 시퀀스 삭제
DROP SEQUENCE PRODUCT_PRODUCT_ID_SEQ;
DROP SEQUENCE IMAGE_IMAGE_ID_SEQ;
DROP SEQUENCE COURSE_COURSE_ID_SEQ;


-- 테이블 생성
CREATE TABLE product(
        product_id           NUMBER(10),
        member_id              NUMBER(10),
        category              varchar2(30)         NOT NULL,
        title                 varchar2(90)         NOT NULL,
        guide_yn              char(1)           NOT NULL,
        normal_price           NUMBER(7)         NOT NULL,
        guide_price           NUMBER(7)          NOT NULL,
        sales_price           NUMBER(7)          NOT NULL,
        sales_guide_price         NUMBER(7)          NOT NULL,
        total_day             NUMBER(2)         NOT NULL,
        total_time             NUMBER(2)          NOT NULL,
        req_money              NUMBER(7)           NOT NULL,
        sleep_info           char(1),
        transport_info        varchar2(45),
        food_info              char(1),
        req_people            varchar2(45)      NOT NULL,
        target              varchar2(45)       NOT NULL,
        stucks              varchar2(90),
        description           varchar2(1500)        NOT NULL,
        detail              varchar2(3000)      NOT NULL,
        file_name              varchar2(255)        NOT NULL,
        file_type              varchar2(50)       NOT NULL,
        file_size              NUMBER             NOT NULL,
        file_data              BLOB             NOT NULL,
        price_detail         varchar2(450)        NOT NULL,
        gprice_detail         varchar2(450)        NOT NULL,
        status               varchar2(12)         NOT NULL,
        create_date                              date,
        update_date                              date
);

-- 제약 조건 추가
ALTER TABLE product ADD PRIMARY KEY(product_id);
ALTER TABLE product ADD FOREIGN KEY(member_id) REFERENCES member(member_id);
ALTER TABLE product ADD CHECK (category IN ('area','pet','restaurant','culture_history','season_nature','silver_disables'));
ALTER TABLE product ADD CHECK (guide_yn IN ('Y', 'N'));
ALTER TABLE product MODIFY (guide_yn DEFAULT 'N');
ALTER TABLE product MODIFY (normal_price DEFAULT 0);
ALTER TABLE product MODIFY (guide_price DEFAULT 0);
ALTER TABLE product MODIFY (sales_price DEFAULT 0);
ALTER TABLE product MODIFY (sales_guide_price DEFAULT 0);
ALTER TABLE product ADD CHECK (total_day >=0);
ALTER TABLE product MODIFY (total_day DEFAULT 0);
ALTER TABLE product ADD CHECK (total_time >= 0 AND total_time < 24);
ALTER TABLE product MODIFY (total_time DEFAULT 0);
ALTER TABLE product ADD CHECK (req_money >= 0);
ALTER TABLE product MODIFY (req_money DEFAULT 0);
ALTER TABLE product ADD CHECK (sleep_info IN ('Y', 'N'));
ALTER TABLE product MODIFY (sleep_info DEFAULT 'N');
ALTER TABLE product ADD CHECK (food_info IN ('Y', 'N'));
ALTER TABLE product MODIFY (food_info DEFAULT 'N');
ALTER TABLE product ADD CHECK (status IN ('판매중', '판매대기'));
ALTER TABLE product MODIFY (create_date NOT NULL);
ALTER TABLE product MODIFY (create_date DEFAULT sysdate);
ALTER TABLE product MODIFY (update_date DEFAULT sysdate);

-- 시퀀스 생성
CREATE SEQUENCE PRODUCT_PRODUCT_ID_SEQ;

-- 테이블 생성
CREATE TABLE product_image(
        image_id  NUMBER(10),
        product_id  NUMBER(10),
        image_data  BLOB  NOT NULL,
        image_order  NUMBER(2)  NOT NULL,
        file_name  varchar2(255),
        file_size   NUMBER,
        mime_type  varchar2(50),
        upload_time date NOT NULL
);

-- 제약 조건 추가
ALTER TABLE product_image ADD PRIMARY KEY(image_id);
ALTER TABLE product_image ADD FOREIGN KEY(product_id) REFERENCES product(product_id);
ALTER TABLE product_image MODIFY (upload_time DEFAULT sysdate);

-- 시퀀스 생성
CREATE SEQUENCE IMAGE_IMAGE_ID_SEQ;

-- 테이블 생성
CREATE TABLE product_course_point (
        course_point_id  NUMBER(10),
        product_id  NUMBER(10),
        point_order  NUMBER  NOT NULL,
        latitude  NUMBER(9,6)  NOT NULL,
        longitude  NUMBER(9,6)  NOT NULL,
        description   varchar2(500),
        created_at  date
);

-- 제약 조건 추가
ALTER TABLE product_course_point ADD PRIMARY KEY(course_point_id);
ALTER TABLE product_course_point ADD FOREIGN KEY(product_id) REFERENCES product(product_id);
ALTER TABLE product_course_point MODIFY (created_at DEFAULT sysdate);

-- 시퀀스 생성
CREATE SEQUENCE COURSE_COURSE_ID_SEQ;

COMMIT;