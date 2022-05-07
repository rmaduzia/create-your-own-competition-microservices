CREATE TABLE users
(
    user_id        BIGINT                     NOT NULL PRIMARY KEY,
    email          VARCHAR(255)               NOT NULL UNIQUE,
    email_verified BOOLEAN      DEFAULT FALSE NOT NULL,
    image_url      VARCHAR(255) DEFAULT NULL,
    password       VARCHAR(255)               NOT NULL,
    provider       VARCHAR(255),
    provider_id    VARCHAR(255)


);

CREATE TABLE user_detail
(
    user_user_id BIGINT NOT NULL PRIMARY KEY,
    age          INT,
    city         VARCHAR(255),
    GENDER       VARCHAR(255),
    user_name    VARCHAR(255) UNIQUE,

    CONSTRAINT FK_USER_DETAIL_USER_ID
        FOREIGN KEY (user_user_id)
            REFERENCES users (user_id)
);