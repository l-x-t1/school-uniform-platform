<template>
  <div class="app-container settlement-apply-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card total">
          <div class="summary-label">当前页申请金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.applyAmount) }}</div>
          <div class="summary-extra">申请笔数 {{ settlementApplyList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card pending">
          <div class="summary-label">待审核金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.pendingAmount) }}</div>
          <div class="summary-extra">待审核 {{ pageSummary.pendingCount }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card approved">
          <div class="summary-label">审核通过金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.approvedAmount) }}</div>
          <div class="summary-extra">通过 {{ pageSummary.approvedCount }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card rejected">
          <div class="summary-label">审核拒绝金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.rejectedAmount) }}</div>
          <div class="summary-extra">拒绝 {{ pageSummary.rejectedCount }} 笔</div>
        </div>
      </el-col>
    </el-row>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="96px"
    >
      <el-form-item label="申请时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          clearable
        />
      </el-form-item>
      <el-form-item label="申请单号" prop="withdrawNo">
        <el-input
          v-model="queryParams.withdrawNo"
          placeholder="请输入申请单号"
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
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="审核通过" value="1" />
          <el-option label="审核拒绝" value="2" />
          <el-option label="已打款" value="3" />
          <el-option label="已取消" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-alert
      title="首版说明：申请结算列表复用供应商提现申请数据；审核通过仅更新申请状态，实际结算动作将在后续结算管理中处理。"
      type="info"
      :closable="false"
      show-icon
      class="rule-alert"
    />

    <el-table v-loading="loading" :data="settlementApplyList" border>
      <el-table-column label="申请单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
      <el-table-column label="申请金额" width="130" align="right">
        <template slot-scope="scope">
          ¥{{ formatMoney(scope.row.applyAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="160" align="center">
        <template slot-scope="scope">
          {{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="审核人" prop="auditUserName" min-width="120" show-overflow-tooltip />
      <el-table-column label="审核时间" width="160" align="center">
        <template slot-scope="scope">
          {{ parseTime(scope.row.auditTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="关联流水" prop="financeFlowId" width="110" align="center" />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:settlementApply:query']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="openDetail(scope.row)"
          >
            详情
          </el-button>
          <el-button
            v-if="String(scope.row.status) === '0'"
            v-hasPermi="['school:settlementApply:audit']"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="openAudit(scope.row)"
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

    <el-dialog title="申请结算详情" :visible.sync="detailOpen" width="920px" append-to-body>
      <div class="dialog-scroll settlement-detail-content">
        <div class="detail-hero">
          <div class="detail-main">
            <div class="detail-eyebrow">结算申请</div>
            <div class="detail-title">{{ detail.withdrawNo || '-' }}</div>
            <div class="detail-meta">
              <span class="meta-pill">{{ detail.supplierName || '未知供应商' }}</span>
              <el-tag :type="statusTagType(detail.status)" size="small">
                {{ formatStatus(detail.status) }}
              </el-tag>
            </div>
          </div>
          <div class="amount-block">
            <div class="amount-label">申请金额</div>
            <div class="amount-value">{{ formatAmountText(detail.applyAmount) }}</div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-head">
            <div>
              <h4>申请与审核</h4>
              <p>结算申请、审核和打款进度</p>
            </div>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">申请单号</span>
              <span class="detail-value">{{ detail.withdrawNo || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">供应商名称</span>
              <span class="detail-value">{{ detail.supplierName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">申请时间</span>
              <span class="detail-value">{{ parseTime(detail.applyTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">审核时间</span>
              <span class="detail-value">{{ parseTime(detail.auditTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">审核人</span>
              <span class="detail-value">{{ detail.auditUserName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">打款时间</span>
              <span class="detail-value">{{ parseTime(detail.payTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
            </div>
            <div class="detail-item full">
              <span class="detail-label">审核备注</span>
              <span class="detail-value">{{ detail.auditRemark || '-' }}</span>
            </div>
            <div class="detail-item full">
              <span class="detail-label">备注</span>
              <span class="detail-value">{{ detail.remark || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-head">
            <div>
              <h4>收款信息</h4>
              <p>供应商提现收款账户</p>
            </div>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">收款户名</span>
              <span class="detail-value">{{ detailBankInfo.accountName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">开户银行</span>
              <span class="detail-value">{{ detailBankInfo.bankName }}</span>
            </div>
            <div class="detail-item full">
              <span class="detail-label">银行账号</span>
              <span class="detail-value">{{ detailBankInfo.accountNo }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog title="审核申请结算" :visible.sync="auditOpen" width="520px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="96px">
        <el-form-item label="申请单号">
          <el-input v-model="auditForm.withdrawNo" disabled />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input :value="auditForm.supplierName || '-'" disabled />
        </el-form-item>
        <el-form-item label="申请金额">
          <el-input :value="'¥' + formatMoney(auditForm.applyAmount)" disabled />
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
        <el-button type="primary" :loading="auditSubmitting" @click="submitAudit">
          提交审核
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  auditSettlementApply,
  getSettlementApply,
  listSettlementApply
} from '@/api/school/settlementApply'

export default {
  name: 'SettlementApply',
  data() {
    return {
      loading: false,
      showSearch: true,
      detailOpen: false,
      auditOpen: false,
      auditSubmitting: false,
      total: 0,
      dateRange: [],
      settlementApplyList: [],
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: undefined,
        supplierId: undefined,
        supplierName: undefined,
        status: undefined
      },
      auditForm: {
        id: undefined,
        withdrawNo: '',
        supplierName: '',
        applyAmount: 0,
        status: '1',
        auditRemark: ''
      },
      auditRules: {
        status: [
          { required: true, message: '请选择审核结果', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    pageSummary() {
      const rows = this.settlementApplyList || []
      const pendingRows = rows.filter((item) => String(item.status) === '0')
      const approvedRows = rows.filter((item) => String(item.status) === '1')
      const rejectedRows = rows.filter((item) => String(item.status) === '2')
      return {
        applyAmount: this.sumAmount(rows, 'applyAmount'),
        pendingAmount: this.sumAmount(pendingRows, 'applyAmount'),
        pendingCount: pendingRows.length,
        approvedAmount: this.sumAmount(approvedRows, 'applyAmount'),
        approvedCount: approvedRows.length,
        rejectedAmount: this.sumAmount(rejectedRows, 'applyAmount'),
        rejectedCount: rejectedRows.length
      }
    },
    detailBankInfo() {
      return this.formatBankInfo(this.detail.bankInfoJson)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSettlementApply(this.addDateRange({ ...this.queryParams }, this.dateRange)).then((response) => {
        this.settlementApplyList = response.rows || []
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
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    openDetail(row) {
      getSettlementApply(row.id).then((response) => {
        this.detail = response.data || {}
        this.detailOpen = true
      })
    },
    openAudit(row) {
      this.auditForm = {
        id: row.id,
        withdrawNo: row.withdrawNo,
        supplierName: row.supplierName,
        applyAmount: row.applyAmount,
        status: '1',
        auditRemark: row.auditRemark || ''
      }
      this.auditOpen = true
      this.$nextTick(() => {
        if (this.$refs.auditForm) {
          this.$refs.auditForm.clearValidate()
        }
      })
    },
    submitAudit() {
      this.$refs.auditForm.validate((valid) => {
        if (!valid) {
          return
        }
        const statusText = this.auditForm.status === '1' ? '审核通过' : '审核拒绝'
        this.$modal.confirm(`确认将该申请结算记录标记为“${statusText}”吗？`).then(() => {
          this.auditSubmitting = true
          return auditSettlementApply({
            id: this.auditForm.id,
            status: this.auditForm.status,
            auditRemark: this.auditForm.auditRemark
          })
        }).then(() => {
          this.$modal.msgSuccess('审核成功')
          this.auditOpen = false
          this.getList()
        }).catch(() => {
        }).finally(() => {
          this.auditSubmitting = false
        })
      })
    },
    sumAmount(list, field) {
      return list.reduce((sum, item) => {
        const value = Number(item[field] || 0)
        return sum + (Number.isNaN(value) ? 0 : value)
      }, 0)
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    formatAmountText(value) {
      if (value === undefined || value === null || value === '') {
        return '-'
      }
      return `¥${this.formatMoney(value)}`
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
    },
    formatStatus(value) {
      const textMap = {
        '0': '待审核',
        '1': '审核通过',
        '2': '审核拒绝',
        '3': '已打款',
        '4': '已取消'
      }
      return textMap[String(value)] || value || '-'
    },
    statusTagType(value) {
      const typeMap = {
        '0': 'warning',
        '1': 'success',
        '2': 'danger',
        '3': 'success',
        '4': 'info'
      }
      return typeMap[String(value)] || 'info'
    }
  }
}
</script>

<style scoped>
.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  border-radius: 12px;
  padding: 18px 20px;
  color: #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  margin-bottom: 16px;
}

.summary-card.total {
  background: linear-gradient(135deg, #2563eb, #0f766e);
}

.summary-card.pending {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.summary-card.approved {
  background: linear-gradient(135deg, #22c55e, #15803d);
}

.summary-card.rejected {
  background: linear-gradient(135deg, #ef4444, #b91c1c);
}

.summary-label {
  font-size: 14px;
  opacity: 0.92;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  margin: 10px 0 6px;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.9;
}

.rule-alert {
  margin-bottom: 14px;
}

.dialog-scroll {
  max-height: calc(88vh - 130px);
  overflow-y: auto;
  padding-right: 4px;
}

.settlement-detail-content {
  color: #1f2937;
}

.detail-hero {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  border: 1px solid #e5edf7;
  border-radius: 8px;
  background: #f8fbff;
}

.detail-main {
  min-width: 0;
}

.detail-eyebrow {
  font-size: 13px;
  color: #64748b;
  line-height: 20px;
}

.detail-title {
  margin-top: 6px;
  color: #0f172a;
  font-size: 20px;
  font-weight: 700;
  line-height: 28px;
  word-break: break-all;
}

.detail-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  min-height: 24px;
  padding: 0 10px;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
  color: #1d4ed8;
  background: #eff6ff;
  font-size: 12px;
  line-height: 22px;
  word-break: break-all;
}

.amount-block {
  display: flex;
  flex: 0 0 180px;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  border-left: 1px solid #dbe7f3;
  padding-left: 18px;
}

.amount-label {
  color: #64748b;
  font-size: 13px;
  line-height: 20px;
}

.amount-value {
  margin-top: 8px;
  color: #0f766e;
  font-size: 26px;
  font-weight: 700;
  line-height: 34px;
}

.detail-section {
  margin-top: 16px;
  border: 1px solid #e5edf7;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #e5edf7;
  background: #f8fafc;
}

.section-head h4 {
  margin: 0;
  color: #10233f;
  font-size: 16px;
  font-weight: 700;
  line-height: 24px;
}

.section-head p {
  margin: 2px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.detail-item {
  display: grid;
  grid-template-columns: 104px minmax(0, 1fr);
  gap: 12px;
  min-height: 48px;
  padding: 12px 16px;
  border-bottom: 1px solid #edf2f7;
}

.detail-item:nth-child(odd) {
  border-right: 1px solid #edf2f7;
}

.detail-item.full {
  grid-column: 1 / -1;
  border-right: 0;
}

.detail-item:last-child {
  border-bottom: 0;
}

.detail-label {
  color: #64748b;
  line-height: 24px;
}

.detail-value {
  min-width: 0;
  color: #1f2937;
  line-height: 24px;
  word-break: break-all;
}

@media (max-width: 768px) {
  .detail-hero {
    flex-direction: column;
  }

  .amount-block {
    align-items: flex-start;
    flex-basis: auto;
    border-left: 0;
    border-top: 1px solid #dbe7f3;
    padding-top: 14px;
    padding-left: 0;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-item,
  .detail-item:nth-child(odd),
  .detail-item.full,
  .detail-item.full:nth-last-child(-n + 2) {
    border-right: 0;
    border-bottom: 1px solid #edf2f7;
  }

  .detail-item:last-child {
    border-bottom: 0;
  }
}
</style>
