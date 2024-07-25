ALTER TABLE competitions RENAME COLUMN competition_end TO event_end_date;
ALTER TABLE competitions RENAME COLUMN competition_name TO event_name;
ALTER TABLE competitions RENAME COLUMN competition_start TO event_start_date;
ALTER TABLE competitions RENAME COLUMN competition_owner TO event_owner;
ALTER TABLE competitions RENAME COLUMN street TO street_name;