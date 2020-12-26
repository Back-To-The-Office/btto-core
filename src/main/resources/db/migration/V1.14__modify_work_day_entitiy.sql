ALTER TABLE workday RENAME TO work_session;

ALTER TABLE work_session RENAME work_date TO start_date_time;

ALTER TABLE work_session DROP COLUMN duration_sec;

ALTER TABLE work_session ADD COLUMN session_date DATE DEFAULT NOW();
ALTER TABLE work_session ADD COLUMN end_date_time TIMESTAMP without time zone DEFAULT NULL;
ALTER TABLE work_session ADD COLUMN timezone_offset NUMERIC;
ALTER TABLE work_session ADD COLUMN day_sequence_num NUMERIC;
