# RuoYi 代码生成器使用说明

## 当前已完成

本地 MySQL `xfglpt` 已完成以下处理：

1. 已导入 20 张新增业务表。
2. 已补齐 RuoYi 代码生成器元数据表：
   - `gen_table`
   - `gen_table_column`
3. 已将 20 张新增业务表预导入到 `gen_table` / `gen_table_column`。
4. 已修正 Nacos 中 `xfglpt-gen-dev.yml` 的本地生成器配置：
   - 数据库：`127.0.0.1:3306/xfglpt`
   - 用户名：`root`
   - 密码：`123456`
   - 默认包路径：`com.xfgl.school`
   - 表前缀提示：`xfpt_`
5. 已调整生成器 `GenUtils`，避免 MySQL 8 下 `int` / `tinyint` 同步数据库后被识别成 `Long`。
6. 20 张新增业务表的主键均已实现自增：
   - 数据库字段：`id bigint NOT NULL AUTO_INCREMENT`
   - 主键约束：`PRIMARY KEY (id)`
   - 生成器字段：`is_pk = 1`、`is_increment = 1`
   - 生成实体：RuoYi 模板会生成 `@TableId(type = IdType.AUTO)`

## 已生成的配套 SQL

1. `D:\xiaofu\修改数据库\20张业务表创建SQL.sql`
   - 创建 20 张新增业务表。

2. `D:\xiaofu\修改数据库\RuoYi代码生成器元数据表修复.sql`
   - 补齐/修复 `gen_table`、`gen_table_column`。

3. `D:\xiaofu\修改数据库\RuoYi代码生成器预导入20表.sql`
   - 将 20 张表预导入代码生成器。

4. `D:\xiaofu\修改数据库\RuoYi代码生成器Nacos配置修正.sql`
   - 修正本地 Nacos 中 `xfglpt-gen-dev.yml`。

## 代码生成器里已配置的 20 张表

| 表名 | 生成类名 | 模板 | 子表 |
|---|---|---|---|
| `xfpt_goods` | `XfptGoods` | 主子表 | `xfpt_goods_size(goods_id)` |
| `xfpt_goods_size` | `XfptGoodsSize` | 单表 | - |
| `xfpt_school_supplier_rel` | `XfptSchoolSupplierRel` | 主子表 | `xfpt_school_supplier_goods_rel(rel_id)` |
| `xfpt_school_supplier_goods_rel` | `XfptSchoolSupplierGoodsRel` | 单表 | - |
| `xfpt_group_notice` | `XfptGroupNotice` | 主子表 | `xfpt_group_notice_item(notice_id)` |
| `xfpt_group_notice_item` | `XfptGroupNoticeItem` | 单表 | - |
| `xfpt_supplier_apply` | `XfptSupplierApply` | 单表 | - |
| `xfpt_user_address` | `XfptUserAddress` | 单表 | - |
| `xfpt_cart_item` | `XfptCartItem` | 单表 | - |
| `xfpt_order_info` | `XfptOrderInfo` | 主子表 | `xfpt_order_item(order_id)` |
| `xfpt_order_item` | `XfptOrderItem` | 单表 | - |
| `xfpt_production_order` | `XfptProductionOrder` | 主子表 | `xfpt_production_order_item(production_id)` |
| `xfpt_production_order_item` | `XfptProductionOrderItem` | 单表 | - |
| `xfpt_delivery_order` | `XfptDeliveryOrder` | 主子表 | `xfpt_delivery_order_item(delivery_id)` |
| `xfpt_delivery_order_item` | `XfptDeliveryOrderItem` | 单表 | - |
| `xfpt_after_sale` | `XfptAfterSale` | 单表 | - |
| `xfpt_comment` | `XfptComment` | 主子表 | `xfpt_comment_reply(comment_id)` |
| `xfpt_comment_reply` | `XfptCommentReply` | 单表 | - |
| `xfpt_finance_flow` | `XfptFinanceFlow` | 单表 | - |
| `xfpt_withdraw_apply` | `XfptWithdrawApply` | 单表 | - |

## 推荐使用方式

1. 启动本地服务：
   - Nacos
   - `xfglpt-gateway`
   - `xfglpt-auth`
   - `xfglpt-system`
   - `xfglpt-gen`
   - 前端 `xiaofu-PC端/xiaofu-xx`

2. 如果 Nacos 或 `xfglpt-gen` 在修改前已经启动过，需要重启它们。
   - 原因：本次是直接修正了本地 Nacos 数据库里的 `xfglpt-gen-dev.yml`。

3. 登录 PC 管理端后进入：
   - `系统工具 -> 代码生成`

4. 现在无需再点“导入表”，20 张表已经在代码生成列表中。

5. 先点“预览”，确认生成内容。

6. 建议使用“zip压缩包”生成代码。
   - zip 会包含 Java、Mapper XML、Vue、API JS、菜单 SQL。
   - “自定义路径”模式当前后端只写 Java/Mapper XML，不写 Vue 和菜单 SQL。

## 生成后放置位置

后端推荐合并到：

- Java：`D:\xiaofu\经贸大学校服项目\xfpt\xfglpt-modules\xfglpt-shcool\src\main\java\com\xfgl\school`
- Mapper XML：`D:\xiaofu\经贸大学校服项目\xfpt\xfglpt-modules\xfglpt-shcool\src\main\resources\mapper`

前端推荐合并到：

- API：`D:\xiaofu\xiaofu-PC端\xiaofu-xx\src\api\school`
- 页面：`D:\xiaofu\xiaofu-PC端\xiaofu-xx\src\views\school`

说明：当前项目模块目录名是 `xfglpt-shcool`，但 Java 包名是 `com.xfgl.school`。生成器配置保持 `school`，匹配现有权限前缀和前端目录习惯。

## 字段控件注意事项

1. 状态字段已默认设为 `radio`，类型字段已默认设为 `select`。
2. `main_image`、`receipt_qr_code` 已默认设为 `imageUpload`。
3. JSON 和富文本字段默认不在列表/查询展示，避免页面列表过重。
4. `payee_bank_account_no` 默认不在列表/查询展示，避免银行卡号明文暴露。
5. 字典类型还没有批量写入 `sys_dict_type` / `sys_dict_data`，生成前最好在字段信息里补字典。

## 资金流水状态

`xfpt_finance_flow.status` 当前为三种状态：

| 值 | 含义 |
|---|---|
| `0` | 未支付 |
| `1` | 已到达学校账号 |
| `2` | 已到达供应商账号 |

支付展示逻辑：

1. 统购订单：`order_type = 1`，展示学校收款码，到账后流水状态改为 `1`。
2. 零售订单：`order_type = 2`，展示供应商收款码，到账后流水状态改为 `2`。

## 验证结果

已用临时库 `xfglpt_ddl_check` 从空库验证：

- 20 张业务表创建成功。
- 20 张表预导入生成器成功。
- 生成器字段元数据共 417 条。
- 没有 `unsigned` 字段。

编译验证执行过：

```powershell
mvn -pl xfglpt-modules/xfglpt-gen -am -DskipTests compile
```

编译失败原因是项目当前 JDK/Lombok 兼容问题：

```text
java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree$JCImport does not have member field 'com.sun.tools.javac.tree.JCTree qualid'
```

这不是本次数据库或生成器配置导致的错误。需要切换到项目兼容 JDK，或升级 Lombok 后再完整编译。
