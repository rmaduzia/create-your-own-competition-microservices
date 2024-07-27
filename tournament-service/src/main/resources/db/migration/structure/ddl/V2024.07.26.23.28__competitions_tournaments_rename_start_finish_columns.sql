ALTER TABLE tournaments RENAME COLUMN is_started TO is_event_started;
ALTER TABLE tournaments RENAME COLUMN is_finished TO is_event_finished;
ALTER TABLE competitions RENAME COLUMN is_started TO is_event_started;
ALTER TABLE competitions RENAME COLUMN is_finished TO is_event_finished;