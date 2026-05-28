-- Add supplier blacklist and complaint supervision menu for education bureau roles.
-- Role keys: jyj = district education bureau, sjyj = city education bureau.
-- The page reads blacklist records and complaint feedback; blacklist detail APIs
-- require school:hmd:list and school:hmd:query permissions.

SET @new_feature_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE path = 'newFeature'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

SET @supplier_supervision_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'jiaoyuju/supplierSupervision/index'
     OR (@new_feature_menu_id IS NOT NULL AND parent_id = @new_feature_menu_id AND path = 'supplierSupervision')
  ORDER BY menu_id
  LIMIT 1
);

SET @supplier_supervision_menu_id := IFNULL(
  @supplier_supervision_menu_id,
  (SELECT IFNULL(MAX(menu_id), 3000) + 1 FROM sys_menu)
);

INSERT INTO sys_menu (
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  query,
  is_frame,
  is_cache,
  menu_type,
  visible,
  status,
  perms,
  icon,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT
  @supplier_supervision_menu_id,
  CONVERT(0xE4BE9BE5BA94E59586E9BB91E5908DE58D95E4B88EE68A95E8AF89 USING utf8mb4),
  @new_feature_menu_id,
  3,
  'supplierSupervision',
  'jiaoyuju/supplierSupervision/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'school:hmd:list',
  'list',
  'system',
  NOW(),
  NULL,
  NULL,
  CONVERT(0xE69599E882B2E5B180E7ABAFE4BE9BE5BA94E59586E9BB91E5908DE58D95E4B88EE68A95E8AF89E79B91E79DA3 USING utf8mb4)
WHERE @new_feature_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_id = @supplier_supervision_menu_id
       OR component = 'jiaoyuju/supplierSupervision/index'
       OR (parent_id = @new_feature_menu_id AND path = 'supplierSupervision')
  );

SET @supplier_supervision_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE component = 'jiaoyuju/supplierSupervision/index'
     OR (@new_feature_menu_id IS NOT NULL AND parent_id = @new_feature_menu_id AND path = 'supplierSupervision')
  ORDER BY menu_id
  LIMIT 1
);

UPDATE sys_menu
SET
  menu_name = CONVERT(0xE4BE9BE5BA94E59586E9BB91E5908DE58D95E4B88EE68A95E8AF89 USING utf8mb4),
  parent_id = IF(@new_feature_menu_id IS NOT NULL, @new_feature_menu_id, parent_id),
  path = 'supplierSupervision',
  component = 'jiaoyuju/supplierSupervision/index',
  menu_type = 'C',
  visible = '0',
  status = '0',
  perms = 'school:hmd:list',
  icon = 'list',
  update_time = NOW()
WHERE menu_id = @supplier_supervision_menu_id;

SET @hmd_query_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE parent_id = @supplier_supervision_menu_id
    AND perms = 'school:hmd:query'
  ORDER BY menu_id
  LIMIT 1
);

SET @hmd_query_menu_id := IFNULL(
  @hmd_query_menu_id,
  (SELECT IFNULL(MAX(menu_id), 3000) + 1 FROM sys_menu)
);

INSERT INTO sys_menu (
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  query,
  is_frame,
  is_cache,
  menu_type,
  visible,
  status,
  perms,
  icon,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT
  @hmd_query_menu_id,
  CONVERT(0xE9BB91E5908DE58D95E8AFA6E68385 USING utf8mb4),
  @supplier_supervision_menu_id,
  1,
  '',
  NULL,
  NULL,
  1,
  0,
  'F',
  '0',
  '0',
  'school:hmd:query',
  '#',
  'system',
  NOW(),
  NULL,
  NULL,
  ''
WHERE @supplier_supervision_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = @supplier_supervision_menu_id
      AND perms = 'school:hmd:query'
  );

SET @hmd_query_menu_id := (
  SELECT menu_id
  FROM sys_menu
  WHERE parent_id = @supplier_supervision_menu_id
    AND perms = 'school:hmd:query'
  ORDER BY menu_id
  LIMIT 1
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, @new_feature_menu_id
FROM sys_role r
WHERE (
    LOWER(r.role_key) LIKE '%jyj%'
    OR LOWER(r.role_key) LIKE '%jiaoyuju%'
    OR r.role_name LIKE CONCAT('%', CONVERT(0xE69599E882B2E5B180 USING utf8mb4), '%')
  )
  AND @new_feature_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = @new_feature_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, @supplier_supervision_menu_id
FROM sys_role r
WHERE (
    LOWER(r.role_key) LIKE '%jyj%'
    OR LOWER(r.role_key) LIKE '%jiaoyuju%'
    OR r.role_name LIKE CONCAT('%', CONVERT(0xE69599E882B2E5B180 USING utf8mb4), '%')
  )
  AND @supplier_supervision_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = @supplier_supervision_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, @hmd_query_menu_id
FROM sys_role r
WHERE (
    LOWER(r.role_key) LIKE '%jyj%'
    OR LOWER(r.role_key) LIKE '%jiaoyuju%'
    OR r.role_name LIKE CONCAT('%', CONVERT(0xE69599E882B2E5B180 USING utf8mb4), '%')
  )
  AND @hmd_query_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = @hmd_query_menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m ON m.perms IN ('school:hmd:list', 'school:hmd:query')
WHERE (
    LOWER(r.role_key) LIKE '%jyj%'
    OR LOWER(r.role_key) LIKE '%jiaoyuju%'
    OR r.role_name LIKE CONCAT('%', CONVERT(0xE69599E882B2E5B180 USING utf8mb4), '%')
  )
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = m.menu_id
  );
