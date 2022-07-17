CREATE TABLE IF NOT EXISTS email
(
    id              bigint auto_increment   PRIMARY KEY,
    receiver        VARCHAR(255)            NOT NULL,
    subject         VARCHAR(130)            NOT NULL,
    content         VARCHAR(255)            NOT NULL,
    status          VARCHAR(10)             NOT NULL,
    creation_time   TIMESTAMP               NOT NULL    DEFAULT now(),
    update_time     TIMESTAMP               NOT NULL    DEFAULT now()
);