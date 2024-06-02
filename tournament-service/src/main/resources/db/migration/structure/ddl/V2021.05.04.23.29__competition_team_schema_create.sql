CREATE TABLE competition_team
(
    competition_id BIGINT NOT NULL,
    team_id        BIGINT NOT NULL,

    PRIMARY KEY (competition_id, team_id),

    KEY FK_COMPETITION_TEAM_TEAM_ID (team_id),

    CONSTRAINT FK_COMPETITION_TEAM_TEAM_ID
        FOREIGN KEY (team_id)
            REFERENCES teams (id),

    CONSTRAINT FK_COMPETITION_TEAM_COMPETITION_ID
        FOREIGN KEY (competition_id)
            REFERENCES competitions (id)
);