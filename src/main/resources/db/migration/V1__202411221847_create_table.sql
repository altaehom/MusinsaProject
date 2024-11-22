CREATE TABLE `brand`
(
  `id`      bigint(20)    AUTO_INCREMENT  NOT NULL,
  `brand_name`    varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted`   bit(1)         DEFAULT 0  NOT NULL,
  `created_at`  datetime(6)               NOT NULL COMMENT '생성 시간',
  `updated_at`  datetime(6)               DEFAULT NULL COMMENT '수정 시간',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
 DEFAULT CHARSET = utf8mb4
 COLLATE = utf8mb4_unicode_ci;

 CREATE TABLE `category`
 (
   `id`      bigint(20)    AUTO_INCREMENT  NOT NULL,
   `category_name`    varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
   `deleted`   bit(1)         DEFAULT 0  NOT NULL,
   `created_at`  datetime(6)               NOT NULL COMMENT '생성 시간',
   `updated_at`  datetime(6)               DEFAULT NULL COMMENT '수정 시간',
   PRIMARY KEY (`id`),
   UNIQUE INDEX UDX_category__category_name(category_name)
 ) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

  CREATE TABLE `product`
  (
    `id`      bigint(20)    AUTO_INCREMENT  NOT NULL,
    `brand_id`      bigint(20)     NOT NULL,
    `category_id`   bigint(20)     NOT NULL,
    `price`   DECIMAL(10, 2)   DEFAULT 0  NOT NULL,
    `deleted`   bit(1)         DEFAULT 0  NOT NULL,
    `created_at`  datetime(6)               NOT NULL COMMENT '생성 시간',
    `updated_at`  datetime(6)               DEFAULT NULL COMMENT '수정 시간',
    PRIMARY KEY (`id`),
  	INDEX IDX_product__brand_id(brand_id),
  	INDEX IDX_product__category_id(category_id)
  ) ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4
   COLLATE = utf8mb4_unicode_ci;