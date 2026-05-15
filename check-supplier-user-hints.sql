SELECT table_name, column_name
FROM information_schema.columns
WHERE table_schema='xfglpt' AND (table_name LIKE '%supplier%' OR column_name LIKE '%user_name%' OR column_name LIKE '%login%')
AND table_name LIKE '%supplier%'
ORDER BY table_name, ordinal_position;
