<template>
  <div class="app-container supplier-withdraw-record-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-title">{{ heroTitle }}</div>
      <div class="hero-subtitle">
        {{ heroSubtitle }}
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
        <div class="summary-card paid">
          <div class="summary-label">已打款</div>
          <div class="summary-value">{{ pageSummary.paidCount }}</div>
          <div class="summary-extra">金额 ¥{{ formatMoney(pageSummary.paidAmount) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card ended">
          <div class="summary-label">已拒绝/已取消</div>
          <div class="summary-value">{{ pageSummary.closedCount }}</div>
          <div class="summary-extra">已结束记录</div>
        </div>
      </el-col>
    </el-row>

    <div class="panel-card">
      <div class="table-header">
        <div>
          <div class="table-title">{{ heroTitle }}</div>
          <div class="table-desc">教育局端按区域查看供应商申请结算记录，只读展示，不执行学校审核动作。</div>
        </div>
      </div>

      <el-form
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="86px"
      >
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
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请日期" prop="applyTime">
          <el-date-picker
            v-model="queryParams.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            clearable
            placeholder="请选择申请日期"
          />
        </el-form-item>
        <el-form-item label="审核日期" prop="auditTime">
          <el-date-picker
            v-model="queryParams.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            clearable
            placeholder="请选择审核日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">
            查询
          </el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="withdrawApplyList" border class="record-table">
        <el-table-column label="提现单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="供应商名称" prop="supplierName" min-width="170" show-overflow-tooltip />
        <el-table-column v-if="showSchoolColumn" label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
        <el-table-column label="申请金额" width="120" align="right">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.applyAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" :type="statusTagType(scope.row.status)">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="120" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.applyTime, "{y}-{m}-{d}") || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="审核人" prop="auditUserName" min-width="120" show-overflow-tooltip />
        <el-table-column label="打款时间" width="170" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.payTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}
          </template>
        </el-table-column>
        <el-table-column v-if="canQueryWithdrawDetail" label="详情" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openDetail(scope.row)">
              查看
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

    <el-dialog :title="detailDialogTitle" :visible.sync="detailOpen" width="760px" append-to-body>
      <div v-loading="detailLoading">
        <div class="detail-section">
          <div class="section-title">业务信息</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="提现单号">{{ formatValue(detail.withdrawNo) }}</el-descriptions-item>
            <el-descriptions-item label="提现状态">
              <el-tag size="mini" :type="statusTagType(detail.status)">
                {{ formatStatus(detail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="供应商名称">{{ formatValue(detail.supplierName) }}</el-descriptions-item>
            <el-descriptions-item label="学校名称">{{ formatValue(detail.schoolName) }}</el-descriptions-item>
            <el-descriptions-item label="申请金额">¥{{ formatMoney(detail.applyAmount) }}</el-descriptions-item>
            <el-descriptions-item label="关联流水号">{{ formatValue(detail.flowNo) }}</el-descriptions-item>
            <el-descriptions-item label="订单号">{{ formatValue(detail.orderNo) }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ parseTime(detail.applyTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ parseTime(detail.auditTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
            <el-descriptions-item label="审核人">{{ formatValue(detail.auditUserName) }}</el-descriptions-item>
            <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">补充信息</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="审核备注" :span="2">{{ formatValue(detail.auditRemark) }}</el-descriptions-item>
            <el-descriptions-item label="收款信息" :span="2">
              <div class="bank-info">
                <div>收款户名：{{ formatBankInfo(detail.bankInfoJson).accountName }}</div>
                <div>开户银行：{{ formatBankInfo(detail.bankInfoJson).bankName }}</div>
                <div>银行账号：{{ formatBankInfo(detail.bankInfoJson).accountNo }}</div>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ formatValue(detail.remark) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <div class="section-title">内部字段</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="供应商ID">{{ formatValue(detail.supplierId) }}</el-descriptions-item>
            <el-descriptions-item label="关联流水ID">{{ formatValue(detail.financeFlowId) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import auth from '@/plugins/auth'
import { getWithdrawApply, listWithdrawApply } from '@/api/school/withdrawApply'

const statusOptions = [
  { label: '待审核', value: '0' },
  { label: '审核通过', value: '1' },
  { label: '审核拒绝', value: '2' },
  { label: '已打款', value: '3' },
  { label: '已取消', value: '4' }
]

const createQueryParams = () => ({
  pageNum: 1,
  pageSize: 10,
  withdrawNo: undefined,
  supplierName: undefined,
  status: undefined,
  applyTime: undefined,
  auditTime: undefined
})

export default {
  name: 'SupplierWithdrawRecord',
  props: {
    heroTitle: {
      type: String,
      default: '教育局端申请结算列表'
    },
    heroSubtitle: {
      type: String,
      default: '教育局端查看供应商提交的申请结算记录，不执行学校审核动作。'
    },
    detailDialogTitle: {
      type: String,
      default: '教育局端申请结算详情'
    }
  },
  data() {
    return {
      loading: false,
      detailLoading: false,
      detailOpen: false,
      total: 0,
      withdrawApplyList: [],
      detail: {},
      statusOptions,
      queryParams: createQueryParams()
    }
  },
  computed: {
    settlementViewMode() {
      return (this.$route.meta && this.$route.meta.settlementViewMode) || 'education'
    },
    showSchoolColumn() {
      return this.settlementViewMode === 'education'
    },
    canViewWithdrawList() {
      return auth.hasPermi('school:withdrawApply:list')
    },
    canQueryWithdrawDetail() {
      return auth.hasPermi('school:withdrawApply:query')
    },
    pageSummary() {
      const rows = this.withdrawApplyList || []
      const pendingRows = rows.filter((item) => String(item.status) === '0')
      const paidRows = rows.filter((item) => String(item.status) === '3')
      const closedRows = rows.filter((item) => ['2', '4'].includes(String(item.status)))
      return {
        applyAmount: this.sumAmount(rows, 'applyAmount'),
        pendingAmount: this.sumAmount(pendingRows, 'applyAmount'),
        pendingCount: pendingRows.length,
        paidAmount: this.sumAmount(paidRows, 'applyAmount'),
        paidCount: paidRows.length,
        closedCount: closedRows.length
      }
    }
  },
  created() {
    if (this.canViewWithdrawList) {
      this.getList()
    }
  },
  methods: {
    buildQueryParams() {
      return {
        ...this.queryParams,
        viewMode: this.settlementViewMode
      }
    },
    getList() {
      if (!this.canViewWithdrawList) {
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
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams = createQueryParams()
      this.getList()
    },
    openDetail(row) {
      if (!this.canQueryWithdrawDetail) {
        return
      }
      this.detailOpen = true
      this.detailLoading = true
      getWithdrawApply(row.id, { viewMode: this.settlementViewMode })
        .then((response) => {
          this.detail = response.data || {}
        })
        .finally(() => {
          this.detailLoading = false
        })
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
      return matched ? matched.label : value || '-'
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
  background: linear-gradient(90deg, #eef5ff 0%, #effaf4 100%);
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
  color: #475569;
  line-height: 1.7;
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
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.summary-card.pending {
  background: linear-gradient(135deg, #f59e0b, #fb8c00);
}

.summary-card.paid {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.summary-card.ended {
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
