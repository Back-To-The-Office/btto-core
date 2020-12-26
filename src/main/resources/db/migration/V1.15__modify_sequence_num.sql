ALTER TABLE work_session DROP COLUMN timezone_offset;
ALTER TABLE work_session DROP COLUMN day_sequence_num;

ALTER TABLE work_session ADD COLUMN timezone_offset BIGINT;
ALTER TABLE work_session ADD COLUMN day_sequence_num INTEGER;
