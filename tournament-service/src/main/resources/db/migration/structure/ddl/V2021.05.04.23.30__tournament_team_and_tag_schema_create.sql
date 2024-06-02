CREATE TABLE tournament_teams
(
    tournament_id BIGINT NOT NULL,
    team_id       BIGINT NOT NULL,

    PRIMARY KEY (tournament_id, team_id),

    CONSTRAINT FK_TOURNAMENT_TEAMS_TOURNAMENT_ID
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id),

    CONSTRAINT FK_TOURNAMENT_TEAMS_TEAM_ID
        FOREIGN KEY (team_id)
            REFERENCES teams (id)

);




CREATE TABLE tournament_tags
(
    tournament_id BIGINT NOT NULL,
    tag_id        BIGINT NOT NULL,

    PRIMARY KEY (tournament_id, tag_id),

    CONSTRAINT FK_TOURNAMENT_TAGS_TOURNAMENT_ID
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id),

    CONSTRAINT FK_TOURNAMENT_TAGS_TAG_ID
        FOREIGN KEY (tag_id)
            REFERENCES tags (id)

);