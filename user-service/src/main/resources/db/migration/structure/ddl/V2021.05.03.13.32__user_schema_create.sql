CREATE TABLE user_detail_opinion_about_users
(
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name      VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    opinion        VARCHAR(255) NOT NULL
);