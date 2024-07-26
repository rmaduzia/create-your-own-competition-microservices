ALTER TABLE tournaments RENAME COLUMN street TO street_name;
ALTER TABLE tournaments RENAME COLUMN tournament_name TO event_name;
ALTER TABLE tournaments RENAME COLUMN tournament_owner TO event_owner;
ALTER TABLE tournaments RENAME COLUMN tournament_start TO event_start_date;
ALTER TABLE tournaments RENAME COLUMN tournament_end TO event_end_date;