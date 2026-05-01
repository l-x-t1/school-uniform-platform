# QrOpenController 接口文档（含实体字段）

- Controller 源码: `/Users/yht/IdeaProjects/xghlpt/xfglpt-modules/xfglpt-shcool/src/main/java/com/xfgl/school/controller/QrOpenController.java`
- 基础路径: `/qr/open`
- 请求方式: 全部 `GET`

## 1. 通用规则

### 1.1 分页参数（列表接口通用）

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| pageNum | Integer | 否 | 页码，默认 `1` |
| pageSize | Integer | 否 | 每页条数，默认 `10` |
| orderByColumn | String | 否 | 排序字段 |
| isAsc | String | 否 | 排序方向：`asc` / `desc` |
| reasonable | Boolean | 否 | 分页合理化 |

### 1.2 列表返回 `TableDataInfo`

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 0,
  "rows": []
}
```

### 1.3 详情返回 `AjaxResult`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 1.4 BaseEntity 继承字段（大部分实体可作为查询参数）

| 字段 | 类型 | 说明 |
|---|---|---|
| searchValue | String | 搜索值 |
| createBy | String | 创建人 |
| createTime | Date | 创建时间 |
| updateBy | String | 更新人 |
| updateTime | Date | 更新时间 |
| remark | String | 备注 |
| delFlag | String | 删除标记 |
| params | Map<String,Object> | 扩展参数 |

## 2. 接口参数清单（逐个接口）

### 2.1 流程接口

- 接口: `GET /qr/open/cglclist`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deptId | Long | 是 | 部门 ID，代码中强校验不能为空 |
| 其他查询字段 | 见 `XfptPurchaseProcess` | 否 | 通过 QueryString 按同名字段绑定 |

- 返回: `TableDataInfo<XfptPurchaseProcess>`

### 2.2 班子会议列表

- 接口: `GET /qr/open/list`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deptId | Long | 否 | 不传时默认当前登录人部门 |
| 其他查询字段 | 见 `TeamMeeting` | 否 | QueryString 绑定 |

- 返回: `TableDataInfo<TeamMeeting>`

### 2.3 班子会议详情

- 接口: `GET /qr/open/bzhy/{id}`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 班子会议主键 |

- 返回: `AjaxResult<TeamMeeting>`（附带 `timeAxisList`、`teamMeetingSonList`）

### 2.4 家长购买意见征集列表

- 接口: `GET /qr/open/gmyjzjlist`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| 查询字段 | 见 `XfptJzBuyOpinions` | 否 | 代码内会将 `deptId` 置空进行查询 |

- 返回: `TableDataInfo<XfptJzBuyOpinions>`（含投票人数、投票率、状态计算）

### 2.5 家长购买意见征集详情

- 接口: `GET /qr/open/gmyjzj/{id}`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 征集主键 |

- 返回: `AjaxResult<XfptJzBuyOpinions>`（附带 `sonDataList`、`gradeClassList`、`sonList`）

### 2.6 问卷表头

- 接口: `GET /qr/open/headingList`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| questionId | Long | 是 | 问卷配置 ID |

- 返回: `AjaxResult<List<LableValue>>`

### 2.7 签名投票列表

- 接口: `GET /qr/open/signatureList`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| questId | Long | 是 | 问卷/业务 ID |
| pageNum | Long | 是 | 当前页（该接口手动分页） |
| pageSize | Long | 是 | 每页大小（该接口手动分页） |

- 返回: `TableDataInfo<Map<String,Object>>`

### 2.8 选用组织名单报备与公开列表

- 接口: `GET /qr/open/xyzz/list`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| 查询字段 | 见 `XfptOrgReportDisclose` | 否 | 代码内将 `deptId` 置空 |

- 返回: `TableDataInfo<XfptOrgReportDisclose>`

### 2.9 选用组织名单报备与公开详情

- 接口: `GET /qr/open/xyzz/{id}`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 主键 |

- 返回: `AjaxResult<XfptOrgReportDisclose>`（附带 `sonDataList`）

### 2.10 确定选购模式会议列表

- 接口: `GET /qr/open/qdxgms/list`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| 查询字段 | 见 `XfptReportMode` | 否 | 代码内将 `deptId` 置空 |

- 返回: `TableDataInfo<XfptReportMode>`

### 2.11 确定选购模式会议详情

- 接口: `GET /qr/open/qdxgms/{id}`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 主键 |

- 返回: `AjaxResult<XfptReportModeVo>`

### 2.12 公告列表

- 接口: `GET /qr/open/notice/list`
- 参数: `XfptOrganizationMode` 实体字段（QueryString）
- 返回: `TableDataInfo<XfptOrganizationMode>`

### 2.13 公告详情

- 接口: `GET /qr/open/notice/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptOrganizationMode>`

### 2.14 企业入围公告列表

- 接口: `GET /qr/open/rwgg/list`
- 参数: `XfptRwggzs` 实体字段（QueryString）
- 返回: `TableDataInfo<XfptRwggzs>`

### 2.15 企业入围公告详情

- 接口: `GET /qr/open/rwgg/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptRwggzs>`（附带 `supplierList`）

### 2.16 评定小组确定供应商列表

- 接口: `GET /qr/open/pdxzcs/list`
- 参数: `XfptPdxzSureSupplier` 实体字段（QueryString）
- 返回: `TableDataInfo<XfptPdxzSureSupplier>`（含投票率）

### 2.17 评定小组确定供应商详情

- 接口: `GET /qr/open/pdxzcs/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptPdxzSureSupplierVo>`

### 2.18 选购结果公示报备与公开列表

- 接口: `GET /qr/open/xgjg/list`
- 参数: `XfptAnnoResults` 实体字段（QueryString，代码内会将 `deptId` 置空）
- 返回: `TableDataInfo<XfptAnnoResults>`

### 2.19 选购结果公示报备与公开详情

- 接口: `GET /qr/open/xgjg/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptAnnoResults>`（附带 `sonDataList`）

### 2.20 合同关联列表

- 接口: `GET /qr/open/cght/list`
- 参数: `XfptSuppliceContract` 实体字段（QueryString，代码内会将 `deptId` 置空）
- 返回: `TableDataInfo<XfptSuppliceContract>`

### 2.21 合同从表列表

- 接口: `GET /qr/open/cghtson/list`
- 参数:

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| suppliceContractId | String | 建议必传 | 子表查询关联主键，代码中会用它查询主合同 |
| 其他查询字段 | 见 `XfptSuppliceContractSon` | 否 | QueryString 绑定 |

- 返回: `TableDataInfo<XfptSuppliceContractSon>`

### 2.22 检验报告关联列表

- 接口: `GET /qr/open/zjbg/list`
- 参数: `XfptSuppliceInspectionReport` 实体字段（QueryString，代码内会将 `deptId` 置空）
- 返回: `TableDataInfo<XfptSuppliceInspectionReport>`

### 2.23 检验报告从表列表

- 接口: `GET /qr/open/zjbgson/list`
- 参数: `XfptSuppliceInspectionReportSon` 实体字段（QueryString）
- 返回: `TableDataInfo<XfptSuppliceInspectionReportSon>`

### 2.24 家长满意度总结列表

- 接口: `GET /qr/open/jzmyd/list`
- 参数: `XfptPatriarchSatisfactionSummarize` 实体字段（QueryString，代码内会将 `deptId` 置空）
- 返回: `TableDataInfo<XfptPatriarchSatisfactionSummarize>`（含状态/投票率计算）

### 2.25 家长满意度总结详情

- 接口: `GET /qr/open/jzmyd/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptPatriarchSatisfactionSummarize>`（附带年级、问卷配置、问卷题目）

### 2.26 采购信息报备详情

- 接口: `GET /qr/open/cgxxbb/{id}`
- 参数: Path `id: Long`（必填，按 `teamId` 查询）
- 返回: `AjaxResult<XfptProcSummary>`

### 2.27 采购工作总结列表

- 接口: `GET /qr/open/cggzzj/list`
- 参数: `XfptCglcgzzj` 实体字段（QueryString）
- 返回: `TableDataInfo<XfptCglcgzzj>`

### 2.28 采购工作总结详情

- 接口: `GET /qr/open/cggzzj/{id}`
- 参数: Path `id: Long`（必填）
- 返回: `AjaxResult<XfptCglcgzzj>`

## 3. 入参实体字段字典（重点）

说明: 以下字段均可通过 QueryString 同名传参绑定。

### 3.1 XfptPurchaseProcess

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| purchaseName | String | 采购名称 |
| purchaseYear | String | 采购年份 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| xfptSchoolGradeClass | List<XfptSchoolGradeClass> | 年级班级列表 |
| cgTime | String | 采购时间 |
| teamId | Long | 班子会议 ID |

### 3.2 TeamMeeting

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| title | String | 会议标题 |
| meetingTime | Date | 会议时间 |
| meetingUser | String | 会议参会人 |
| zw | String | 职务 |
| dblb | String | 代表类别 |
| zzName | String | 组织名称 |
| hyjy | String | 会议纪要 |
| currOrder | Long | 当前流程顺序 |
| timeTb | String | 时间轴原始串 |
| gyfwnx | String | 供货服务年限 |
| jyjStatus | Integer | 教育局状态 |
| teamMeetingSonList | List<TeamMeetingSon> | 子项 |
| sonJsonData | String | 子项 JSON |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| timeAxisList | List<String> | 时间轴拆分值 |
| teamId | Long | 关联流程 ID |

### 3.3 XfptJzBuyOpinions

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| teamMeetingId | Long | 班子会议 ID |
| title | String | 标题 |
| zjlx | String | 征集类型 |
| xzrq | Date | 截止日期 |
| yjzjqk | String | 意见征集情况 |
| zw | String | 职务 |
| dblb | String | 代表类别 |
| xm | String | 姓名 |
| tpqzsj | String | 投票起止时间（通常 `start#end`） |
| txxx | String | 填写选项 |
| fwnx | String | 服务年限 |
| configId | Long | 配置 ID |
| configContent | String | 配置内容 |
| fj | String | 附件 |
| sonDataList | List<XfptJzBuyOpinionsSon> | 子表 |
| gradeClassList | List<XfptBuyGradeClass> | 年级班级 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| questionConfigId | Long | 问卷配置 ID |
| questionBusinessId | Long | 问卷业务 ID |
| names | String | 名单 |
| count | Integer | 应投人数 |
| votedCount | Integer | 已投人数 |
| votingRate | double | 投票率 |
| sonList | List<XfptQuestionnaireSurvey> | 问卷题目列表 |

### 3.4 XfptOrgReportDisclose

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| teamMeetingId | Long | 班子会议 ID |
| title | String | 标题 |
| total | String | 总计 |
| jzxsDb | String | 家长学生代表 |
| jzxsZb | String | 家长学生占比 |
| sonDataList | List<XfptOrgReportDiscloseSon> | 子表 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| file | String | 附件 |

### 3.5 XfptReportMode

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| title | String | 标题 |
| purchaseMode | String | 采购模式编码 |
| reportForm | String | 报告形式 |
| ancestors | String | 祖级路径 |
| deptId | Long | 部门 ID |
| teamId | Long | 班子会议/流程 ID |

### 3.6 XfptOrganizationMode

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| title | String | 标题 |
| isSamplesPic | String | 是否样品图 |
| samplesPic | String | 样品图 |
| otherFile | String | 其他附件 |
| noticeFile | String | 公告文件 |
| type | String | 类型 |
| quesId | Long | 问卷 ID |
| bmjzTime | Date | 报名截止时间 |
| isRelease | String | 是否发布 |
| inviteFile | String | 邀请文件 |
| teamId | Long | 班子会议/流程 ID |
| ancestors | String | 祖级路径 |
| deptId | Long | 部门 ID |
| startTime | Date | 开始时间 |
| endTime | Date | 结束时间 |
| supplierList | List<XfptSupplierList> | 供应商列表 |

### 3.7 XfptRwggzs

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| name | String | 公告名称 |
| isRelease | String | 是否发布 |
| file | String | 附件 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| teamId | Long | 班子会议/流程 ID |
| supplierList | List<XfptSupplierList> | 供应商列表 |
| type | String | 类型 |

### 3.8 XfptPdxzSureSupplier

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| teamId | Long | 班子会议/流程 ID |
| ancestors | String | 祖级路径 |
| deptId | Long | 部门 ID |
| title | String | 标题 |
| userFile | String | 用户文件 |
| questId | String | 问卷 ID |
| otherFile | String | 其他附件 |
| type | String | 类型 |
| names | String | 名单 |
| count | Integer | 应投人数 |
| votedCount | Integer | 已投人数 |
| votingRate | double | 投票率 |

### 3.9 XfptAnnoResults

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| noticeTitle | String | 公示标题 |
| procurementNoticeName | String | 采购公告名称 |
| procurementNoticeId | String | 采购公告 ID |
| transactionCompanyName | String | 成交公司名称 |
| bidPrice | String | 中标价格 |
| judgesList | String | 评委名单 |
| judgesCount | Long | 评委数量 |
| announcementFile | String | 公示附件 |
| otherFile | String | 其他附件 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| teamId | Long | 班子会议/流程 ID |
| fileType | String | 文件类型 |
| isRelease | String | 是否发布 |
| reportMode | String | 选购模式（文本） |
| cgsj | Date | 采购时间 |
| noticeFile | String | 公告文件 |

### 3.10 XfptSuppliceContract

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| deptId | String | 部门 ID |
| deptName | String | 部门名称 |
| supplierId | String | 供应商 ID |
| supplierName | String | 供应商名称 |
| supplierPhone | String | 供应商电话 |
| teamMeetingId | String | 班子会议 ID |
| ancestors | String | 祖级路径 |
| fzr | String | 负责人 |
| fzrdh | String | 负责人电话 |
| count | Integer | 数量 |
| dj | Double | 单价 |
| zj | Double | 总价 |
| studentNumber | String | 学生人数 |
| legalRepresentative | String | 法人代表 |

### 3.11 XfptSuppliceContractSon

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| suppliceContractId | String | 合同主表 ID |
| contractName | String | 合同名称 |
| contractDeadline | String | 合同期限 |
| url | String | 文件地址 |
| fileName | String | 文件名 |
| fileType | String | 文件类型 |
| supplierPrincipal | String | 供应商负责人 |
| supplierPhone | String | 供应商电话 |
| numberOrderers | String | 订购人数 |
| unitPrice | String | 单价（文本） |
| totalAmount | String | 总金额（文本） |
| fzr | String | 负责人 |
| fzrdh | String | 负责人电话 |
| count | Integer | 数量 |
| dj | Double | 单价 |
| zj | Double | 总价 |
| teamMeetingSonList | List<TeamMeetingSon> | 会议子项 |
| integrity | String | 完整性校验值 |

### 3.12 XfptSuppliceInspectionReport

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| deptId | String | 部门 ID |
| deptName | String | 部门名称 |
| supplierId | String | 供应商 ID |
| supplierName | String | 供应商名称 |
| supplierPhone | String | 供应商电话 |
| teamMeetingId | String | 班子会议 ID |
| ancestors | String | 祖级路径 |
| legalRepresentative | String | 法人代表 |
| ggTitle | String | 公告标题 |

### 3.13 XfptSuppliceInspectionReportSon

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| inspectionReportTitle | String | 报告标题 |
| url | String | 文件地址 |
| reportId | String | 关联主表 ID |
| fileName | String | 文件名 |
| fileType | String | 文件类型 |
| type | String | 类型 |

### 3.14 XfptPatriarchSatisfactionSummarize

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| summarizeReportName | String | 总结报告名称 |
| summarizeReportType | String | 报告类型（线上/线下） |
| startTime | Date | 开始时间 |
| endTime | Date | 结束时间 |
| questionnaireSurveyId | String | 问卷配置 ID |
| surveyFileUrl | String | 问卷文件地址 |
| surveyFileName | String | 问卷文件名 |
| state | String | 活动状态 |
| deptId | String | 部门 ID |
| deptName | String | 部门名称 |
| teamMeetingId | String | 班子会议 ID |
| ancestors | String | 祖级路径 |
| amount | Long | 数量 |
| xfptBuyGradeClassList | List<XfptBuyGradeClass> | 年级班级 |
| xfptQuestionnaireConfig | XfptQuestionnaireConfig | 问卷配置 |
| xfptQuestionnaireSurveys | List<XfptQuestionnaireSurvey> | 问卷题目 |
| votingNotice | String | 投票公告 |
| names | String | 名单 |
| count | Integer | 应投人数 |
| votedCount | Integer | 已投人数 |
| votingRate | double | 投票率 |
| sonList | List<XfptQuestionnaireSurvey> | 问卷子项 |

### 3.15 XfptProcSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| title | String | 标题 |
| money | Long | 金额 |
| inviteFile | String | 邀请文件 |
| winBidFile | String | 中标文件 |
| enterprise | String | 企业信息 |
| count | Long | 数量 |
| schoolUniformFile | String | 校服文件 |
| schoolName | String | 学校名称 |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |
| buyTime | Date | 采购时间 |
| deptName | String | 部门名称 |
| schoolType | String | 学校类型 |
| teamId | Long | 班子会议/流程 ID |
| dj | Double | 单价 |

### 3.16 XfptCglcgzzj

| 字段 | 类型 | 说明 |
|---|---|---|
| id | Long | 主键 |
| title | String | 标题 |
| file | String | 文件 |
| teamId | Long | 班子会议/流程 ID |
| deptId | Long | 部门 ID |
| ancestors | String | 祖级路径 |

## 4. 详情返回中常见附加实体（便于联调）

### 4.1 TeamMeetingSon

| 字段 | 类型 |
|---|---|
| id | Long |
| teamMeetingId | Long |
| cgnj | String |
| cgnjId | Long |
| xfpl | String |
| gmts | String |
| jgqj | String |
| bz | String |
| teamMeetingIdList | List<Long> |

### 4.2 XfptJzBuyOpinionsSon

| 字段 | 类型 |
|---|---|
| id | Long |
| pId | Long |
| cgnj | String |
| cgnjId | Long |
| xfpl | String |
| xsngmts | String |
| jgqj | String |
| bz | String |
| pidList | List<Long> |

### 4.3 XfptBuyGradeClass

| 字段 | 类型 |
|---|---|
| id | Long |
| gradeClassId | Long |
| gradeClassName | String |
| amount | Long |
| category | Long |
| pid | Long |
| pidList | List<Long> |
| parentId | Long |

### 4.4 XfptQuestionnaireSurvey

| 字段 | 类型 |
|---|---|
| id | Long |
| surveyContent | String |
| surveyResult | String |
| answeringMethod | String |
| questionConfigId | Long |
| xfptQuestionnaireResults | List<XfptQuestionnaireResult> |
| amount | int |
| isRequired | String |
| sm | String |

### 4.5 XfptQuestionnaireConfig

| 字段 | 类型 |
|---|---|
| id | Long |
| title | String |
| useStatus | Long |
| category | String |
| fbStatus | Long |
| beginTime | Date |
| endTime | Date |
| type | String |
| deptId | Long |
| ancestors | String |
| sonList | List<XfptQuestionnaireSurvey> |
| state | String |
| multipleChoice | int |
| multipleResponse | int |
| text | int |
| wjStatus | String |
| votingDescription | String |
| votingNotice | String |
| names | String |
| lcCategory | String |
| deptIds | List<String> |

### 4.6 XfptSupplierList

| 字段 | 类型 |
|---|---|
| id | Long |
| name | String |
| price | String |
| file | String |
| modeId | String |
| deptId | Long |
| ancestors | String |
| videoUrl | String |
| url | String |

### 4.7 XfptOrgReportDiscloseSon

| 字段 | 类型 |
|---|---|
| id | Long |
| pId | Long |
| zw | String |
| dblb | String |
| xm | String |
| phone | String |
| njZy | String |
| bj | String |

### 4.8 XfptReportModeOper

| 字段 | 类型 |
|---|---|
| id | Long |
| czr | String |
| ip | String |
| reportModeId | String |

### 4.9 VO

`XfptReportModeVo`

| 字段 | 类型 |
|---|---|
| xfptReportMode | XfptReportMode |
| operList | List<XfptReportModeOper> |

`XfptPdxzSureSupplierVo`

| 字段 | 类型 |
|---|---|
| xfptPdxzSureSupplier | XfptPdxzSureSupplier |
| xfptQuestionnaireConfig | XfptQuestionnaireConfig |

`LableValue`

| 字段 | 类型 |
|---|---|
| label | String |
| value | String |

## 5. 调用示例

### 5.1 列表查询示例

```http
GET /qr/open/gmyjzjlist?pageNum=1&pageSize=20&title=2026年夏季校服意见征集
```

### 5.2 详情查询示例

```http
GET /qr/open/gmyjzj/123
```

### 5.3 问卷签名列表示例

```http
GET /qr/open/signatureList?questId=1001&pageNum=1&pageSize=10
```
