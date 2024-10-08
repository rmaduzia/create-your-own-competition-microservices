CREATE TABLE competitions
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    city                VARCHAR(255),
    competition_end     DATETIME,
    competition_name    VARCHAR(255),
    competition_start   DATETIME,
    is_open_recruitment BOOLEAN,
    max_amount_users    INT,
    owner               VARCHAR(255),
    street              VARCHAR(255),
    street_number       INT,
    max_amount_of_teams INT,
    competition_owner   VARCHAR(255)
);

CREATE TABLE match_in_competition
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_team_name     VARCHAR(255),
    second_team_name    VARCHAR(255),
    is_closed           BOOLEAN,
    is_match_was_played BOOLEAN,
    is_winner_confirmed BOOLEAN,
    match_date          DATETIME,
    winner_team         VARCHAR(255),
    competition_id      BIGINT
);



CREATE TABLE votes_for_winning_team_in_competition_matches
(

    match_in_competition_id BIGINT       NOT NULL,
    user_name               VARCHAR(255) NOT NULL,
    team_name               VARCHAR(255) DEFAULT NULL,

    PRIMARY KEY (match_in_competition_id, user_name),

    CONSTRAINT FK_VOTES_FOR_WINNING_TEAM_IN_COMPETITION_MATCHES_COMPETITION_ID
        FOREIGN KEY (match_in_competition_id)
            REFERENCES match_in_competition (id)
);