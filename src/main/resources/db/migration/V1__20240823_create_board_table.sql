CREATE TABLE `board`
(
  `id`      bigint(20)    AUTO_INCREMENT  NOT NULL,
  `title`    varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `body`     text DEFAULT NULL,
  `created_at`  datetime(6)               NOT NULL COMMENT '생성 시간',
  `updated_at`  datetime(6)               DEFAULT NULL COMMENT '수정 시간',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
 DEFAULT CHARSET = utf8mb4
 COLLATE = utf8mb4_unicode_ci;