<template>
  <div class="app-container school-settlement-audit-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-title">学校端申请结算列表</div>
      <div class="hero-subtitle">
        学校端统一查看并审核供应商提交的结算申请，仅处理待审核记录的审核通过与驳回。
      </div>
    </el-card>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card total">
          <div class="summary-label">当前页申请总额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.applyAmount) }}</div>
          <div class="summary-extra">记录数 {{ withdrawApplyList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card pending">
          <div class="summary-label">待审核</div>
          <div class="summary-value">{{ pageSummary.pendingCount }}</div>
          <div class="summary-extra">金额 ¥{{ formatMoney(pageSummary.pendingAmount) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card approved">
          <div class="summary-label">审核通过</div>
          <div class="summary-value">{{ pageSummary.approvedCount }}</div>
          <div class="summary-extra">金额 ¥{{ formatMoney(pageSummary.approvedAmount) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card rejected">
          <div class="summary-label">审核拒绝</div>
          <div class="summary-value">{{ pageSummary.rejectedCount }}</div>
          <div class="summary-extra">已驳回记录</div>
        </div>
      </el-col>
    </el-row>

    <div class="panel-card">
      <div class="table-header">
        <div>
          <div class="table-title">学校端结算审核</div>
          <div class="table-desc">学校端仅保留审核视角，不再展示供应商内部维护操作。</div>
        </div>
        <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
      </div>

      <el-form
        v-show="showSearch"
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="86px"
      >
        <el-form-item v-if="requiresSchoolSelector" label="学校" prop="scopeSchoolId">
          <el-select
            v-model="queryParams.scopeSchoolId"
            filterable
            clearable
            :loading="schoolLoading"
            placeholder="请先选择学校"
          >
            <el-option
              v-for="item in schoolOptions"
              :key="getSchoolId(item)"
              :label="getSchoolName(item)"
              :value="getSchoolId(item)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="提现单号" prop="withdrawNo">
          <el-input
            v-model="queryParams.withdrawNo"
            placeholder="请输入提现单号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间" prop="applyTime">
          <el-date-picker
            v-model="queryParams.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            clearable
            placeholder="请选择申请时间"
          />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="queryParams.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            clearable
            placeholder="请选择审核时间"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="requiresSchoolSelector && !selectedSchoolId"
        class="selector-tip"
        title="请先选择学校，再查看该学校合作供应商的提现申请记录。"
        type="info"
        :closable="false"
      />

      <el-table v-loading="loading" :data="withdrawApplyList" border class="record-table">
        <el-table-column label="提现单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="供应商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
        <el-table-column v-if="showSchoolColumn" label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
        <el-table-column label="申请金额" width="120" align="right">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.applyAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="120" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="110" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="statusTagType(scope.row.status)">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核人" prop="auditUserName" min-width="120" show-overflow-tooltip />
        <el-table-column label="审核时间" width="170" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.auditTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="打款时间" width="170" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.payTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-hasPermi="['school:withdrawApply:query']"
              type="text"
              size="mini"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="canAudit(scope.row)"
              v-hasPermi="['school:withdrawApply:edit']"
              type="text"
              size="mini"
              @click="handleAudit(scope.row)"
            >
              审核
            </el-button>
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
    </div>

    <el-dialog title="学校端申请结算详情" :visible.sync="detailOpen" width="860px" append-to-body>
      <div v-loading="detailLoading">
        <div class="detail-section">
          <div class="section-title">业务信息</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="提现单号">{{ formatValue(detail.withdrawNo) }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag size="mini" :type="statusTagType(detail.status)">
                {{ formatStatus(detail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="供应商名称">{{ formatValue(detail.supplierName) }}</el-descriptions-item>
            <el-descriptions-item label="学校名称">{{ formatValue(detail.schoolName) }}</el-descriptions-item>
            <el-descriptions-item label="申请金额">¥{{ formatMoney(detail.applyAmount) }}</el-descriptions-item>
            <el-descriptions-item label="关联流水号">{{ formatValue(detail.flowNo) }}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{ formatValue(detail.orderNo) }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDateTime(detail.applyTime) }}</el-descriptions-item>
            <el-descriptions-item label="审核人">{{ formatValue(detail.auditUserName) }}</el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ formatDateTime(detail.auditTime) }}</el-descriptions-item>
            <el-descriptions-item label="打款时间">{{ formatDateTime(detail.payTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">审核与收款信息</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="审核备注" :span="2">{{ formatValue(detail.auditRemark) }}</el-descriptions-item>
            <el-descriptions-item label="收款信息" :span="2">
              <div class="bank-info">
                <div>收款户名：{{ formatBankInfo(detail.bankInfoJson).accountName }}</div>
                <div>开户银行：{{ formatBankInfo(detail.bankInfoJson).bankName }}</div>
                <div>银行账号：{{ formatBankInfo(detail.bankInfoJson).accountNo }}</div>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">内部字段</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="供应商ID">{{ formatValue(detail.supplierId) }}</el-descriptions-item>
            <el-descriptions-item label="关联资金流水ID">{{ formatValue(detail.financeFlowId) }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ formatValue(detail.remark) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="学校端审核结算申请" :visible.sync="auditOpen" width="560px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="96px">
        <el-form-item v-if="showSchoolColumn" label="学校名称">
          <el-input :value="formatValue(currentAuditRow.schoolName)" disabled />
        </el-form-item>
        <el-form-item label="提现单号">
          <el-input :value="formatValue(currentAuditRow.withdrawNo)" disabled />
        </el-form-item>
        <el-form-item label="供应商名称">
          <el-input :value="formatValue(currentAuditRow.supplierName)" disabled />
        </el-form-item>
        <el-form-item label="申请金额">
          <el-input :value="`¥${formatMoney(currentAuditRow.applyAmount)}`" disabled />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-input :value="formatDateTime(currentAuditRow.applyTime)" disabled />
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="1">审核通过</el-radio>
            <el-radio label="2">审核拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditOpen = false">取消</el-button>
        <el-button type="primary" :loading="auditSubmitting" @click="submitAudit">提交审核</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  auditWithdrawApply,
  getWithdrawApply,
  listWithdrawApply
} from '@/api/school/withdrawApply'
import { getSchoolListCompleteDetail } from '@/api/jiaoyuju/schoolManagement'

const statusOptions = [
  { label: '待审核', value: '0' },
  { label: '审核通过', value: '1' },
  { label: '审核拒绝', value: '2' },
  { label: '已打款', value: '3' },
  { label: '已取消', value: '4' }
]

const createAuditForm = () => ({
  id: null,
  status: '1',
  auditRemark: ''
})

const createQueryParams = () => ({
  pageNum: 1,
  pageSize: 10,
  withdrawNo: undefined,
  supplierName: undefined,
  status: undefined,
  applyTime: undefined,
  auditTime: undefined,
  scopeSchoolId: undefined
})

export default {
  name: 'SettlementApplyPage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      auditSubmitting: false,
      schoolLoading: false,
      showSearch: true,
      total: 0,
      detailOpen: false,
      auditOpen: false,
      withdrawApplyList: [],
      schoolOptions: [],
      detail: {},
      currentAuditRow: {},
      statusOptions,
      queryParams: createQueryParams(),
      auditForm: createAuditForm(),
      auditRules: {
        status: [
          { required: true, message: '请选择审核结果', trigger: 'change' }
        ],
        auditRemark: [
          { required: true, message: '请输入审核备注', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    settlementViewMode() {
      return (this.$route.meta && this.$route.meta.settlementViewMode) || 'school'
    },
    requiresSchoolSelector() {
      return !!(this.$route.meta && this.$route.meta.requiresSchoolSelector)
    },
    selectedSchoolId() {
      return this.queryParams.scopeSchoolId
    },
    showSchoolColumn() {
      return this.requiresSchoolSelector
    },
    pageSummary() {
      const rows = this.withdrawApplyList || []
      const pendingRows = rows.filter((item) => String(item.status) === '0')
      const approvedRows = rows.filter((item) => String(item.status) === '1')
      const rejectedRows = rows.filter((item) => String(item.status) === '2')
      return {
        applyAmount: this.sumAmount(rows, 'applyAmount'),
        pendingAmount: this.sumAmount(pendingRows, 'applyAmount'),
        pendingCount: pendingRows.length,
        approvedAmount: this.sumAmount(approvedRows, 'applyAmount'),
        approvedCount: approvedRows.length,
        rejectedCount: rejectedRows.length
      }
    }
  },
  created() {
    if (this.requiresSchoolSelector) {
      this.loadSchoolOptions()
      return
    }
    this.getList()
  },
  methods: {
    buildQueryParams() {
      const params = {
        ...this.queryParams,
        viewMode: this.settlementViewMode
      }
      if (!this.requiresSchoolSelector) {
        delete params.scopeSchoolId
      }
      return params
    },
    buildActionPayload(payload = {}) {
      const data = {
        ...payload,
        viewMode: this.settlementViewMode
      }
      if (this.requiresSchoolSelector) {
        data.scopeSchoolId = this.selectedSchoolId
      }
      return data
    },
    ensureSchoolSelected(showMessage = false) {
      if (!this.requiresSchoolSelector) {
        return true
      }
      if (this.selectedSchoolId) {
        return true
      }
      if (showMessage) {
        this.$modal.msgWarning('请先选择学校')
      }
      return false
    },
    getList(showMessage = false) {
      if (!this.ensureSchoolSelected(showMessage)) {
        this.withdrawApplyList = []
        this.total = 0
        return
      }
      this.loading = true
      listWithdrawApply(this.buildQueryParams())
        .then((response) => {
          this.withdrawApplyList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleQuery() {
      if (!this.ensureSchoolSelected(true)) {
        return
      }
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams = createQueryParams()
      if (this.requiresSchoolSelector) {
        this.withdrawApplyList = []
        this.total = 0
        return
      }
      this.getList()
    },
    handleView(row) {
      if (!this.ensureSchoolSelected(true)) {
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      getWithdrawApply(row.id, this.buildQueryParams())
        .then((response) => {
          this.detail = response.data || {}
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    handleAudit(row) {
      if (!this.ensureSchoolSelected(true)) {
        return
      }
      if (!this.canAudit(row)) {
        this.$modal.msgWarning('当前记录不支持审核')
        return
      }
      this.currentAuditRow = { ...row }
      this.auditForm = {
        ...createAuditForm(),
        id: row.id
      }
      this.auditOpen = true
      this.$nextTick(() => {
        this.resetForm('auditForm')
      })
    },
    submitAudit() {
      if (!this.ensureSchoolSelected(true)) {
        return
      }
      this.$refs.auditForm.validate((valid) => {
        if (!valid) {
          return
        }
        this.auditSubmitting = true
        auditWithdrawApply(this.buildActionPayload(this.auditForm))
          .then(() => {
            this.$modal.msgSuccess('审核成功')
            this.auditOpen = false
            this.getList()
            if (this.detailOpen && this.detail.id === this.auditForm.id) {
              this.refreshDetail(this.auditForm.id)
            }
          })
          .finally(() => {
            this.auditSubmitting = false
          })
      })
    },
    refreshDetail(id) {
      this.detailLoading = true
      getWithdrawApply(id, this.buildQueryParams())
        .then((response) => {
          this.detail = response.data || {}
        })
        .finally(() => {
          this.detailLoading = false
        })
    },
    loadSchoolOptions() {
      this.schoolLoading = true
      getSchoolListCompleteDetail({
        pageNum: 1,
        pageSize: 1000,
        parentId: this.$store.getters.deptId,
        isSchool: '1'
      })
        .then((response) => {
          this.schoolOptions = response.rows || response.data || []
        })
        .finally(() => {
          this.schoolLoading = false
        })
    },
    getSchoolName(item = {}) {
      return item.schoolName || item.deptName || item.name || item.label || ''
    },
    getSchoolId(item = {}) {
      if (item.schoolId != null) {
        return item.schoolId
      }
      if (item.deptId != null) {
        return item.deptId
      }
      return item.id
    },
    canAudit(row = {}) {
      return this.settlementViewMode === 'school' && String(row.status) === '0'
    },
    sumAmount(list, field) {
      return (list || []).reduce((sum, item) => {
        const value = Number(item[field] || 0)
        return sum + (Number.isNaN(value) ? 0 : value)
      }, 0)
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    formatValue(value) {
      return value === undefined || value === null || value === '' ? '-' : value
    },
    formatStatus(value) {
      const matched = this.statusOptions.find((item) => item.value === String(value))
      return matched ? matched.label : '-'
    },
    statusTagType(value) {
      const typeMap = {
        '0': 'warning',
        '1': 'success',
        '2': 'danger',
        '3': 'primary',
        '4': 'info'
      }
      return typeMap[String(value)] || 'info'
    },
    formatDateTime(value) {
      return this.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}') || '-'
    },
    formatBankInfo(bankInfoJson) {
      const emptyInfo = {
        accountName: '-',
        bankName: '-',
        accountNo: '-'
      }
      if (!bankInfoJson) {
        return emptyInfo
      }
      try {
        const bankInfo = JSON.parse(bankInfoJson)
        return {
          accountName: bankInfo.accountName || '-',
          bankName: bankInfo.bankName || '-',
          accountNo: bankInfo.accountNo || bankInfo.bankNo || bankInfo.bankAccountNo || '-'
        }
      } catch (error) {
        return emptyInfo
      }
    }
  }
}
</script>

<style scoped>
.hero-card {
  margin-bottom: 20px;
  border-radius: 18px;
  background: linear-gradient(90deg, #eef5ff 0%, #eff8ff 42%, #f4fbf4 100%);
}

.hero-title {
  font-size: 30px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.hero-subtitle {
  margin-top: 12px;
  font-size: 14px;
  line-height: 1.7;
  color: #475569;
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  border-radius: 16px;
  padding: 22px 24px;
  color: #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  margin-bottom: 16px;
}

.summary-card.total {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
}

.summary-card.pending {
  background: linear-gradient(135deg, #f59e0b, #ea580c);
}

.summary-card.approved {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.summary-card.rejected {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.summary-label {
  font-size: 14px;
  opacity: 0.92;
}

.summary-value {
  font-size: 34px;
  font-weight: 700;
  margin: 14px 0 8px;
  line-height: 1;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.9;
}

.panel-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px 24px;
}

.table-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}

.table-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
}

.table-desc {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.selector-tip {
  margin-bottom: 16px;
}

.record-table {
  margin-top: 8px;
}

.detail-section + .detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}

.bank-info {
  line-height: 24px;
  word-break: break-all;
}
</style>
