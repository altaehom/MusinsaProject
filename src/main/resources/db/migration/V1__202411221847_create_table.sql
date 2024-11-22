CREATE TABLE brand (
  id BIGINT AUTO_INCREMENT NOT NULL,
  brand_name VARCHAR(255) NOT NULL,
  deleted BOOLEAN DEFAULT FALSE NOT NULL,
  created_at TIMESTAMP NOT NULL COMMENT '생성 시간',
  updated_at TIMESTAMP DEFAULT NULL COMMENT '수정 시간',
  PRIMARY KEY (id)
);

CREATE TABLE category (
  id BIGINT AUTO_INCREMENT NOT NULL,
  category_name VARCHAR(255) NOT NULL,
  deleted BOOLEAN DEFAULT FALSE NOT NULL,
  created_at TIMESTAMP NOT NULL COMMENT '생성 시간',
  updated_at TIMESTAMP DEFAULT NULL COMMENT '수정 시간',
  PRIMARY KEY (id),
  UNIQUE (category_name)
);

CREATE TABLE product (
  id BIGINT AUTO_INCREMENT NOT NULL,
  brand_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  price DECIMAL(10, 2) DEFAULT 0 NOT NULL,
  deleted BOOLEAN DEFAULT FALSE NOT NULL,
  created_at TIMESTAMP NOT NULL COMMENT '생성 시간',
  updated_at TIMESTAMP DEFAULT NULL COMMENT '수정 시간',
  PRIMARY KEY (id)
);

CREATE INDEX IDX_product__brand_id ON product (brand_id);
CREATE INDEX IDX_product__category_id ON product (category_id);