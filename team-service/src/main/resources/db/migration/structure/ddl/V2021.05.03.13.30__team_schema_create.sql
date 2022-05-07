CREATE TABLE teams
(
    id                  BIGINT PRIMARY KEY,
    city                VARCHAR(255),
    is_open_recruitment BOOLEAN,
    max_amount_members  INT,
    team_name           VARCHAR(30) NOT NULL,
    team_owner          VARCHAR(255)

);

CREATE TABLE team_members
(
    id BIGINT NOT NULL,
    user_name VARCHAR(30) NOT NULL,
    team_id BIGINT NOT NULL,

    PRIMARY KEY (id, team_id),

    CONSTRAINT FK_USER_TEAMS_TEAM_ID
        FOREIGN KEY (team_id)
            REFERENCES teams (id)
);