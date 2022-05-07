CREATE TABLE teams
(
    id                  BIGINT PRIMARY KEY,
    city                VARCHAR(255),
    is_open_recruitment BOOLEAN,
    max_amount_members  INT,
    team_name           VARCHAR(30) NOT NULL,
    team_owner          VARCHAR(255)

);



CREATE TABLE `teams_tags`
(
    team_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,

    PRIMARY KEY (`team_id`, `tag_id`),

    KEY FK_TEAM_TAGS_TAG_ID (tag_id),

    CONSTRAINT FK_TEAM_TAGS_TAG_ID
        FOREIGN KEY (`tag_id`)
            REFERENCES tags (id),

    CONSTRAINT FK_TEAM_TAGS_TEAM_ID
        FOREIGN KEY (`team_id`)
            REFERENCES teams (id)
)