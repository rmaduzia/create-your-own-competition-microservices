
CREATE TABLE user_teams
(
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, team_id),

    CONSTRAINT FK_USER_TEAMS_USER_ID
        FOREIGN KEY (user_id)
            REFERENCES user_detail (user_user_id),

    CONSTRAINT FK_USER_TEAMS_TEAM_ID
        FOREIGN KEY (team_id)
            REFERENCES teams (id)

);

CREATE TABLE user_competitions
(
    user_id        BIGINT NOT NULL,
    competition_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, competition_id),

    CONSTRAINT FK_USER_COMPETITIONS_USER_ID
        FOREIGN KEY (user_id)
            REFERENCES user_detail (user_user_id),

    CONSTRAINT FK_USER_COMPETITIONS_COMPETITION_ID
        FOREIGN KEY (competition_id)
            REFERENCES competitions (id)

);