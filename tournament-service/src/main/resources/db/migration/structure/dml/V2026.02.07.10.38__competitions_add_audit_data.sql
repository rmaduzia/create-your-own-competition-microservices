UPDATE competitions
SET created_by = 'system_user',
    created_date = '2026-02-07 00:00:00'
WHERE created_by IS NULL