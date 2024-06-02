CREATE TABLE competition_tag
(
    competition_id BIGINT NOT NULL,
    tag_id         BIGINT NOT NULL,
    PRIMARY KEY (competition_id, tag_id),

    KEY FK_COMPETITION_TAG_TAGS_ID (tag_id),

    CONSTRAINT FK_COMPETITION_TAG_TAGS_ID
        FOREIGN KEY (tag_id)
            REFERENCES tags (id),

    CONSTRAINT FK_COMPETITION_TAG_COMPETITION_ID
        FOREIGN KEY (competition_id)
            REFERENCES competitions (id)

);