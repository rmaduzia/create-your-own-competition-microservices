CREATE TABLE tournaments
(
    id                  BIGINT NOT NULL PRIMARY KEY,
    city                VARCHAR(255),
    max_amount_of_teams INT,
    street              VARCHAR(255) DEFAULT NULL,
    street_number       INT,
    tournament_name     VARCHAR(255),
    tournament_owner    VARCHAR(255),
    is_finished         BOOLEAN,
    is_started          BOOLEAN,
    tournament_start    TIMESTAMP


);





CREATE TABLE matches_in_tournaments
(
    id                  BIGINT PRIMARY KEY,
    first_team_name     VARCHAR(255),
    second_team_name    VARCHAR(255),
    is_closed           BOOLEAN,
    is_match_was_played BOOLEAN,
    is_winner_confirmed BOOLEAN,
    match_date          DATE,
    winner_team         VARCHAR(255),
    tournament_id       BIGINT


);

CREATE TABLE votes_for_winning_team_in_tournament_matches
(

    match_in_tournament_id BIGINT       NOT NULL,
    user_name              VARCHAR(255) NOT NULL,
    team_name              VARCHAR(255) DEFAULT NULL,

    PRIMARY KEY (match_in_tournament_id, user_name),

    CONSTRAINT FK_VOTES_FOR_WINNING_TEAM_IN_TOURNAMENT_MATCHES_COMPETITION_ID
        FOREIGN KEY (match_in_tournament_id)
            REFERENCES matches_in_tournaments (id)

);


CREATE TABLE matches_in_tournament
(
    id                        BIGINT PRIMARY KEY,
    confirming_winner_counter INT,
    first_team_name           VARCHAR(255),
    second_team_name          VARCHAR(255),
    is_winner_confirmed       BOOLEAN,
    match_date                DATE,
    winner_team               VARCHAR(255),
    tournament_id             BIGINT,
    is_match_was_played       BOOLEAN


);



CREATE TABLE drawed_teams_in_tournament
(
    tournament_id BIGINT       NOT NULL,
    id            VARCHAR(255) NOT NULL,
    duel          VARCHAR(255),

    PRIMARY KEY (tournament_id, id),

    CONSTRAINT FK_DRAWED_TEAMS_IN_TOURNAMENT_TOURNAMENT_ID
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)


);


CREATE TABLE match_times_in_tournament
(
    tournament_id BIGINT       NOT NULL,
    id            VARCHAR(255) NOT NULL,
    match_time    DATE,

    PRIMARY KEY (tournament_id, id),

    CONSTRAINT FK_MATCH_TIMES_IN_TOURNAMENT_TOURNAMENT_ID
        FOREIGN KEY (tournament_id)
            REFERENCES tournaments (id)
);