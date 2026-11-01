<template>
  <div class="app-container audit-page">
    <el-form
      v-show="showSearch"
      :model="queryParams"
      :inline="true"
      size="small"
      label-width="96px"
      class="query-form"
    >
      <el-form-item label="供应商名称">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名单号">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入报名单号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公告标题">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学校名称">
        <el-input
          v-model="queryParams.schoolName"
          placeholder="请输入学校名称"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名状态">
        <el-select
          v-model="queryParams.displayStatus"
          placeholder="请选择报名状态"
          clearable
          style="width: 220px"
          @change="handleQuery"
        >
          <el-option
            v-for="item in displayStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['school:supplierApply:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="supplierApplyList" border class="audit-table">
      <el-table-column label="供应商信息" min-width="240">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.supplierName) }}</div>
            <div class="info-subline">报名单号：{{ formatValue(scope.row.applyNo) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="公告信息" min-width="280">
        <template slot-scope="scope">
          <div class="info-cell">
            <div class="info-title">{{ formatValue(scope.row.noticeTitle) }}</div>
            <div class="info-subline">公告编号：{{ formatValue(scope.row.noticeCode) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="报名状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getApplyStatusTag(getApplyDisplayStatus(scope.row))">
            {{ getApplyDisplayStatus(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名时间" width="160" align="center">
        <template slot-scope="scope">{{ formatDateTime(scope.row.applyTime) }}</template>
      </el-table-column>
      <el-table-column label="审核时间" width="160" align="center">
        <template slot-scope="scope">{{ formatDateTime(scope.row.auditTime) }}</template>
      </el-table-column>
      <el-table-column label="审核意见" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ formatValue(scope.row.auditRemark) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
          <el-button
            v-if="canAudit(scope.row)"
            v-hasPermi="['school:supplierApply:edit']"
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            @click="handlePass(scope.row)"
          >通过</el-button>
          <el-button
            v-if="canAudit(scope.row)"
            v-hasPermi="['school:supplierApply:edit']"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="openRejectDialog(scope.row)"
          >驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog
      title="供应商报名审核详情"
      :visible.sync="detailOpen"
      width="1080px"
      top="5vh"
      append-to-body
      @closed="resetDetail"
    >
      <div v-loading="detailLoading" class="dialog-scroll">
        <div class="detail-section">
          <div class="section-head">
            <div class="section-title">基础信息</div>
            <div class="section-desc">查看供应商、采购公告和当前审核状态</div>
          </div>
          <div class="detail-kv-grid">
            <div class="detail-kv">
              <span class="detail-label">供应商名称</span>
              <span class="detail-value">{{ formatValue(detailForm.supplierName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">报名单号</span>
              <span class="detail-value">{{ formatValue(detailForm.applyNo) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">公告标题</span>
              <span class="detail-value">{{ formatValue(detailForm.noticeTitle) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">公告编号</span>
              <span class="detail-value">{{ formatValue(detailForm.noticeCode) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">学校名称</span>
              <span class="detail-value">{{ formatValue(detailForm.schoolName) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">当前状态</span>
              <span class="detail-value">{{ getApplyDisplayStatus(detailForm) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">报名时间</span>
              <span class="detail-value">{{ formatDateTime(detailForm.applyTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">审核时间</span>
              <span class="detail-value">{{ formatDateTime(detailForm.auditTime) }}</span>
            </div>
            <div class="detail-kv">
              <span class="detail-label">审核意见</span>
              <span class="detail-value">{{ formatValue(detailForm.auditRemark) }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-head">
            <div class="section-title">报名材料快照</div>
            <div class="section-desc">兼容历史数组数据与新版结构化材料快照</div>
          </div>
          <el-empty v-if="!detailMaterials.length" description="暂无报名材料" />
          <div v-else class="material-list">
            <div v-for="(item, index) in detailMaterials" :key="`${item.type}-${index}`" class="material-card">
              <div class="material-header">
                <div>
                  <div class="material-title">{{ item.label || '附件' }}</div>
                  <div class="material-meta">
                    <span>{{ item.required ? '必传材料' : '选传材料' }}</span>
                    <span>来源：{{ formatMaterialSource(item.source) }}</span>
                    <span>快照时间：{{ item.snapshotTime || '--' }}</span>
                  </div>
                </div>
                <el-tag size="mini" :type="item.required ? 'danger' : 'info'">
                  {{ item.required ? '必传' : '选传' }}
                </el-tag>
              </div>
              <image-upload
                v-if="isImageMaterial(item.type)"
                :value="joinMaterialUrls(item.fileUrls)"
                :limit="6"
                disabled
              />
              <file-upload
                v-else
                :value="joinMaterialUrls(item.fileUrls)"
                disabled
              />
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-head">
            <div class="section-title">补充说明</div>
            <div class="section-desc">保留供应商备注、审核意见和历史日志</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">供应商备注</div>
            <div class="detail-text">{{ formatMultiline(detailForm.remark) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">审核意见</div>
            <div class="detail-text">{{ formatMultiline(detailForm.auditRemark) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-block-title">操作日志</div>
            <el-empty v-if="!detailLogs.length" description="暂无日志" />
            <el-timeline v-else>
              <el-timeline-item
                v-for="item in detailLogs"
                :key="item.id"
                :timestamp="formatDateTime(item.actionTime)"
                placement="top"
              >
                <div class="timeline-title">{{ actionText(item.actionType) }}</div>
                <div class="timeline-meta">版本 {{ item.versionNo || '--' }} / 操作人：{{ item.actionUser || '--' }}</div>
                <div class="timeline-remark">{{ item.actionRemark || '--' }}</div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
        <el-button
          v-if="canAudit(detailForm)"
          v-hasPermi="['school:supplierApply:edit']"
          type="warning"
          @click="openRejectDialog(detailForm)"
        >驳回</el-button>
        <el-button
          v-if="canAudit(detailForm)"
          v-hasPermi="['school:supplierApply:edit']"
          type="primary"
          @click="handlePass(detailForm)"
        >审核通过</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="驳回报名"
      :visible.sync="rejectOpen"
      width="520px"
      append-to-body
      @closed="resetRejectForm"
    >
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="88px">
        <el-form-item label="驳回原因" prop="auditRemark">
          <el-input
            v-model="rejectForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="请填写驳回原因，供应商会在原单上修改后重新提交"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectOpen = false">取消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="submitReject">确认驳回</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSupplierApply,
  listSupplierApply,
  listSupplierApplyLogs,
  passSupplierApplyAudit,
  rejectSupplierApplyAudit
} from '@/api/school/supplierApply'
import {
  getApplyDisplayStatus,
  getApplyStatusTag,
  normalizeApplyMaterials
} from '@/utils/supplierApply'

const DISPLAY_STATUS_OPTIONS = [
  { label: '待审核', value: 'PENDING_REVIEW' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已撤回', value: 'WITHDRAWN' },
  { label: '待提交', value: 'DRAFT' }
]

export default {
  name: 'SupplierApplyAudit',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      supplierApplyList: [],
      detailOpen: false,
      detailLoading: false,
      detailForm: {},
      detailMaterials: [],
      detailLogs: [],
      currentDetailId: null,
      rejectOpen: false,
      rejectLoading: false,
      displayStatusOptions: DISPLAY_STATUS_OPTIONS,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierName: '',
        applyNo: '',
        noticeTitle: '',
        schoolName: '',
        displayStatus: ''
      },
      rejectForm: {
        applyId: null,
        auditRemark: ''
      },
      rejectRules: {
        auditRemark: [
          { required: true, message: '请填写驳回原因', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getApplyDisplayStatus,
    getApplyStatusTag,
    buildListQuery() {
      const query = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        supplierName: this.queryParams.supplierName,
        applyNo: this.queryParams.applyNo,
        noticeTitle: this.queryParams.noticeTitle,
        schoolName: this.queryParams.schoolName
      }
      switch (this.queryParams.displayStatus) {
        case 'DRAFT':
          query.applyStatus = '0'
          break
        case 'PENDING_REVIEW':
          query.applyStatus = '1'
          query.qualificationStatus = '0'
          break
        case 'APPROVED':
          query.applyStatus = '1'
          query.qualificationStatus = '1'
          break
        case 'REJECTED':
          query.applyStatus = '1'
          query.qualificationStatus = '2'
          break
        case 'WITHDRAWN':
          query.applyStatus = '2'
          break
        default:
          break
      }
      return query
    },
    getList() {
      this.loading = true
      listSupplierApply(this.buildListQuery()).then((response) => {
        this.supplierApplyList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        supplierName: '',
        applyNo: '',
        noticeTitle: '',
        schoolName: '',
        displayStatus: ''
      }
      this.getList()
    },
    handleExport() {
      this.download('school/supplierApply/export', this.buildListQuery(), `supplierApplyAudit_${new Date().getTime()}.xlsx`)
    },
    canAudit(row = {}) {
      return String(row.applyStatus) === '1' && String(row.qualificationStatus) === '0'
    },
    handleView(row) {
      const applyId = row && row.id
      if (!applyId) {
        this.$modal.msgError('未找到报名记录')
        return
      }
      this.currentDetailId = applyId
      this.detailOpen = true
      this.loadDetail(applyId)
    },
    loadDetail(applyId) {
      this.detailLoading = true
      Promise.all([
        getSupplierApply(applyId),
        listSupplierApplyLogs(applyId)
      ]).then(([detailResponse, logResponse]) => {
        this.detailForm = detailResponse.data || {}
        this.detailMaterials = normalizeApplyMaterials(this.detailForm.applyFilesJson)
        this.detailLogs = logResponse.data || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    handlePass(row) {
      const applyId = row && row.id
      if (!applyId) {
        this.$modal.msgError('未找到报名记录')
        return
      }
      this.$modal.confirm(`确认通过“${row.supplierName || '当前供应商'}”的报名审核吗？`).then(() => {
        return passSupplierApplyAudit({ applyId })
      }).then(() => {
        this.$modal.msgSuccess('审核已通过')
        this.refreshAfterAudit(applyId)
      }).catch(() => {})
    },
    openRejectDialog(row) {
      const applyId = row && row.id
      if (!applyId) {
        this.$modal.msgError('未找到报名记录')
        return
      }
      this.rejectForm = {
        applyId,
        auditRemark: ''
      }
      this.rejectOpen = true
    },
    submitReject() {
      this.$refs.rejectFormRef.validate((valid) => {
        if (!valid) {
          return
        }
        this.rejectLoading = true
        rejectSupplierApplyAudit(this.rejectForm).then(() => {
          this.$modal.msgSuccess('已驳回该报名')
          const { applyId } = this.rejectForm
          this.rejectOpen = false
          this.refreshAfterAudit(applyId)
        }).finally(() => {
          this.rejectLoading = false
        })
      })
    },
    refreshAfterAudit(applyId) {
      this.getList()
      if (this.detailOpen && this.currentDetailId === applyId) {
        this.loadDetail(applyId)
      }
    },
    resetDetail() {
      this.currentDetailId = null
      this.detailForm = {}
      this.detailMaterials = []
      this.detailLogs = []
      this.detailLoading = false
    },
    resetRejectForm() {
      this.rejectLoading = false
      this.rejectForm = {
        applyId: null,
        auditRemark: ''
      }
      if (this.$refs.rejectFormRef) {
        this.$refs.rejectFormRef.resetFields()
      }
    },
    isImageMaterial(type) {
      return type === 'SAMPLE_IMAGE'
    },
    joinMaterialUrls(fileUrls) {
      return Array.isArray(fileUrls) ? fileUrls.filter(Boolean).join(',') : ''
    },
    formatMaterialSource(source) {
      const sourceMap = {
        PROFILE: '供应商主档',
        QUALIFICATION: '资质复用',
        NOTICE_RULE: '公告要求',
        UPLOAD: '本次上传',
        LEGACY: '历史数据'
      }
      return sourceMap[source] || source || '--'
    },
    actionText(actionType) {
      const actionMap = {
        SAVE_DRAFT: '保存草稿',
        SUBMIT: '提交报名',
        WITHDRAW: '撤回报名',
        AUDIT_PASS: '审核通过',
        AUDIT_REJECT: '审核驳回'
      }
      return actionMap[actionType] || actionType || '--'
    },
    formatValue(value) {
      return value == null || value === '' ? '--' : value
    },
    formatDateTime(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') : '--'
    },
    formatMultiline(value) {
      return value == null || value === '' ? '--' : value
    }
  }
}
</script>

<style scoped lang="scss">
.query-form {
  margin-bottom: 16px;
  padding: 18px 20px 6px;
  background: #f8fafc;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.audit-table {
  border-radius: 12px;
  overflow: hidden;
}

.info-cell {
  min-width: 0;
}

.info-title {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.info-subline {
  font-size: 12px;
  line-height: 1.7;
  color: #6b7785;
}

.dialog-scroll {
  max-height: 72vh;
  overflow-y: auto;
  padding-right: 8px;
}

.detail-section {
  margin-bottom: 18px;
  padding: 18px;
  background: #f8fafc;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.section-head {
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #243447;
}

.section-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8798;
}

.detail-kv-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.detail-kv {
  min-width: 0;
  padding: 12px 14px;
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
}

.detail-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #7a8798;
}

.detail-value {
  display: block;
  line-height: 1.6;
  font-size: 14px;
  color: #243447;
  word-break: break-all;
}

.material-list {
  display: grid;
  gap: 16px;
}

.material-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 10px;
}

.material-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.material-title {
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.material-meta {
  margin-top: 6px;
  font-size: 12px;
  color: #7a8798;
}

.material-meta span + span {
  margin-left: 12px;
}

.detail-block + .detail-block {
  margin-top: 18px;
}

.detail-block-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #243447;
}

.detail-text {
  min-height: 42px;
  padding: 12px 14px;
  margin: 0;
  line-height: 1.6;
  color: #6b7785;
  white-space: pre-wrap;
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 8px;
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: #23324a;
}

.timeline-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #7b8899;
}

.timeline-remark {
  margin-top: 8px;
  color: #4b5a6d;
  line-height: 1.7;
}

@media (max-width: 1200px) {
  .detail-kv-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .detail-kv-grid {
    grid-template-columns: 1fr;
  }

  .material-header {
    flex-direction: column;
  }
}
</style>
