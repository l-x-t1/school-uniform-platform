对于原有数据库的修改

## 1. 第一部分 商品管理

商品库管理 合作学校管理 商品尺码管理

### 1.1 `xfpt_goods`

无修改

### 1.2 xfpt_goods_size 商品尺码表

**补 sort_num** 字段

将原来的**size_name**替换为**sort_num**

并设置字段类型为 **int unsigned**   NOT NULL

排序字段 根据商品尺码依次增加 S 1 M 2  L 3 XL 4....

### 1.3  xfpt_goods_publish_log

操作日志整个表删除

### 1.4 xfpt_school_supplier_rel

删除字段:

- service_phone
- cancel_reason
- start_time
- end_time
- dept_id
- ancestors

### 1.5  xfpt_school_supplier_goods_rel

删除字段:

- dept_id
- ancestors





## 第二部分:

### Xfpt_group_notice_item:

- 删除字段sort_num（排序值），删除字段ancestors

### Xfpt_group_notice:

- 删除字段ancestors

### Xfpt_supplier_apply:

- 删除字段ancestors

### Xfpt_user_address:

- 删除字段status(是否禁用)

### 把表xfpt_student_size_ext全部删除



## 第三部分:

### 1.  xfpt_order_info

- 1.删除了ancestors字段
- 2.将total_amount的注释改为 ：订单总金额（衣服价格加上运费）

### 1.  xfpt_order_item

- 1．删除了ancestors字段

### 3. xfpt_production_order

- 1．删除了ancestors字段

### 4. xfpt_production_order_item

 无修改

### 5.xfpt_delivery_order

1.加入了carriage_fee：运费

### 6. xfpt_delivery_order_item

无修改

## 第四部分：学校信息

### 1. xfpt_school_message

学校银行卡账户属于学校基础资料，放在学校信息扩展表 `xfpt_school_message`，不放 `sys_dept`。

新增字段：

- `bank_account_name`：银行账户户名
- `bank_name`：开户银行
- `bank_branch_name`：开户支行
- `bank_account_no`：银行卡号

说明：

- 不使用 JSON 存学校银行卡账户，避免后续查询、校验、脱敏和导出困难。
- 字段允许为空，兼容已有学校历史数据。
- 对应 SQL 脚本：`对原来的数据库修改的sql脚本/xfpt_school_message_add_bank_account.sql`
