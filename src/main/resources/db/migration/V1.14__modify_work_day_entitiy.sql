ALTER TABLE workday RENAME work_date TO start_date_time;

ALTER TABLE workday DROP COLUMN duration_sec;

ALTER TABLE workday ADD COLUMN start_date DATE DEFAULT NOW();
ALTER TABLE workday ADD COLUMN end_date_time TIMESTAMP without time zone DEFAULT NULL;
ALTER TABLE workday ADD COLUMN timezone_offset NUMERIC;
ALTER TABLE workday ADD COLUMN day_sequence_num NUMERIC;
