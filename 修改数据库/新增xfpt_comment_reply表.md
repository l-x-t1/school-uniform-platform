对于那个 xfpt_comment 中的图片保存数据已经是json格式了,不需要修改了

**表名：xfpt_comment_reply**

### 1.用途：

- 承载主评论下的所有回复
- 支持家长、供应商参与回复
- 支持“回复某条回复的人”，但存储结构仍然是单层
- 支持不同角色展示不同标题，如“家长”“供应商”“商家”

###  2.表结构设计

- id bigint：主键，自增
- comment_id bigint：所属主评论 ID，关联 xfpt_comment.id
- order_id bigint：订单 ID 快照，便于按订单查询
- order_item_id bigint：订单明细 ID 快照，便于按商品评价查询
- goods_id bigint：商品 ID 快照
- supplier_id bigint：供应商 ID
- from_user_id bigint：回复人 ID
- from_user_name varchar(100)：回复人姓名快照
- from_role_type char(1)：回复人角色，1家长 2供应商
- from_role_title varchar(20)：回复人角色标题快照，如“家长”“供应商”
- to_user_id bigint：被回复人 ID，可空
- to_user_name varchar(100)：被回复人姓名快照，可空
- to_role_type char(1)：被回复人角色，可空
- to_role_title varchar(20)：被回复人角色标题快照，可空
- content text：回复内容
- status char(1)：状态，0正常 1隐藏
- sort_num int unsigned：同一评论下回复排序值，可空
- create_by varchar(64)、create_time datetime
- update_by varchar(64)、update_time datetime
- remark varchar(500)
- del_flag char(1)：删除标记，0正常 1删除

明确不需要的字段：

- 不加 parent_reply_id
- 不加 root_reply_id
- 不做多层树结构
- 不做楼中楼递归查询

### 3. 展示与交互规则

- 主评论来自 xfpt_comment
- 所有回复来自 xfpt_comment_reply
- 回复主评论：
  - to_user_id / to_user_name 可空，前端展示为普通回复
- 回复某条二级回复：
  - 仍然新增一条 xfpt_comment_reply
  - 只记录 to_user_id / to_user_name / to_role_*
  - 前端展示为“回复 XXX：内容”
  - 仍然归属在同一个主评论下面，不再嵌套进被回复回复之下

排序建议：

- 主评论下回复列表按 create_time asc
- 如果后续需要商家置顶或运营排序，再启用 sort_num

### 4. 索引与约束

建议索引：

- PRIMARY KEY (id)
- KEY idx_xfpt_comment_reply_comment (comment_id, create_time)
- KEY idx_xfpt_comment_reply_order_item (order_item_id, create_time)
- KEY idx_xfpt_comment_reply_from_user (from_user_id)
- KEY idx_xfpt_comment_reply_supplier (supplier_id)
- KEY idx_xfpt_comment_reply_status (status)

不加唯一约束：

- 同一用户在同一评论下允许多次回复
- 同一供应商也允许连续回复

### 5. 与现有 xfpt_comment 的兼容

- xfpt_comment



  继续承担：

  - 主评论
  - 评分
  - 评论内容
  - 评论图片

- reply_content / reply_time 不删除，但仅作为历史兼容字段

- 新页面查询逻辑：

  - 查主评论：xfpt_comment
  - 查单层回复列表：xfpt_comment_reply where comment_id = ? and del_flag = 0
  - 历史兼容：若旧数据只有 reply_content，可组装成一条供应商回复展示在回复区

### 6. 代码侧落地口径

新增：

- XfptCommentReply 实体，继承 BaseEntity
- XfptCommentReplyMapper
- XfptCommentReplyMapper.xml
- IXfptCommentReplyService
- XfptCommentReplyServiceImpl
- XfptCommentReplyController

Java 类型：

- bigint -> Long
- char(1) -> String
- int unsigned -> Integer
- text -> String
- datetime -> Date