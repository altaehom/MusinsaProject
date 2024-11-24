CREATE TABLE outbox (
    id BIGINT AUTO_INCREMENT,
    event_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    header VARCHAR(255) NOT NULL,
    payload VARCHAR(5000) NOT NULL,
    published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL COMMENT '생성 시간',
    processed_at TIMESTAMP NULL,
    PRIMARY KEY (id)
);
CREATE INDEX IDX_outbox__event_type ON outbox (event_type);