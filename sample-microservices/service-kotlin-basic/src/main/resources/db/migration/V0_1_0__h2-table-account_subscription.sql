CREATE TABLE IF NOT EXISTS account_subscription
(
    id              bigint auto_increment   PRIMARY KEY,
    customer_name   VARCHAR(255)            NOT NULL,
    email_address   VARCHAR(130)            NOT NULL,
    phone           VARCHAR(130)            NOT NULL,
    status          VARCHAR(10)             NOT NULL,
    creation_time   TIMESTAMP               NOT NULL    DEFAULT now(),
    update_time     TIMESTAMP               NOT NULL    DEFAULT now()
);