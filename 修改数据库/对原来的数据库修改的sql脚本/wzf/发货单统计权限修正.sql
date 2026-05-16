-- Fix delivery statistics permission.
-- Align the menu permission with backend endpoint /school/deliveryStatistics/list.

-- The backend checks school:deliveryStatistics:list.
-- The old school:tgfh:list permission causes an access denied response.
UPDATE sys_menu
SET perms = 'school:deliveryStatistics:list',
    update_by = 'sql_fix',
    update_time = NOW()
WHERE menu_id IN (3005, 3050)
  AND perms = 'school:tgfh:list';
