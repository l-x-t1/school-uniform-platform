<template>
  <div class="app-container settlement-manage-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card total">
          <div class="summary-label">当前页结算金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.totalAmount) }}</div>
          <div class="summary-extra">结算记录 {{ settlementManageList.length }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card pending">
          <div class="summary-label">待结算金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.pendingAmount) }}</div>
          <div class="summary-extra">待结算 {{ pageSummary.pendingCount }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card settled">
          <div class="summary-label">已结算金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.settledAmount) }}</div>
          <div class="summary-extra">已结算 {{ pageSummary.settledCount }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card flow">
          <div class="summary-label">已生成流水</div>
          <div class="summary-value">{{ pageSummary.flowCount }}</div>
          <div class="summary-extra">当前页结算流水数</div>
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
      <el-form-item label="供应商ID" prop="supplierId">
        <el-input
          v-model="queryParams.supplierId"
          placeholder="请输入供应商ID"
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
      <el-form-item label="结算状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="待结算" value="1" />
          <el-option label="已结算" value="3" />
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
      title="结算规则：仅审核通过的申请可执行结算；结算成功后会生成一条业务类型为“结算”的资金流水。"
      type="info"
      :closable="false"
      show-icon
      class="rule-alert"
    />

    <el-table v-loading="loading" :data="settlementManageList" border>
      <el-table-column label="申请单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商ID" prop="supplierId" width="100" align="center" />
      <el-table-column label="供应商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
      <el-table-column label="结算金额" width="130" align="right">
        <template slot-scope="scope">
          ¥{{ formatMoney(scope.row.applyAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="结算状态" width="110" align="center">
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
      <el-table-column label="审核时间" width="160" align="center">
        <template slot-scope="scope">
          {{ parseTime(scope.row.auditTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="打款时间" width="160" align="center">
        <template slot-scope="scope">
          {{ parseTime(scope.row.payTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="结算流水" prop="financeFlowId" width="110" align="center" />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:settlementManage:query']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="openDetail(scope.row)"
          >
            详情
          </el-button>
          <el-button
            v-if="String(scope.row.status) === '1'"
            v-hasPermi="['school:settlementManage:settle']"
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="openSettle(scope.row)"
          >
            结算
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

    <el-dialog title="结算详情" :visible.sync="detailOpen" width="820px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="申请单号">{{ detail.withdrawNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结算状态">
          <el-tag :type="statusTagType(detail.status)">
            {{ formatStatus(detail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="供应商ID">{{ detail.supplierId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ detail.supplierName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结算金额">¥{{ formatMoney(detail.applyAmount) }}</el-descriptions-item>
        <el-descriptions-item label="结算流水ID">{{ detail.financeFlowId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(detail.applyTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ parseTime(detail.auditTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detail.auditUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detail.auditRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收款信息" :span="2">
          <pre class="json-preview">{{ detail.bankInfoJson || '-' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">结算资金流水</el-divider>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="流水单号">{{ financeFlow.flowNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="外部交易号">{{ financeFlow.tradeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付渠道">{{ financeFlow.payChannel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="流水金额">¥{{ formatMoney(financeFlow.amount) }}</el-descriptions-item>
        <el-descriptions-item label="收款方">{{ financeFlow.payeeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交易时间">{{ parseTime(financeFlow.tradeTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收款银行">{{ financeFlow.payeeBankName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行卡号">{{ financeFlow.payeeBankAccountNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="流水备注" :span="2">{{ financeFlow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="执行结算" :visible.sync="settleOpen" width="520px" append-to-body>
      <el-form ref="settleForm" :model="settleForm" :rules="settleRules" label-width="108px">
        <el-form-item label="申请单号">
          <el-input v-model="settleForm.withdrawNo" disabled />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input :value="settleForm.supplierName || '-'" disabled />
        </el-form-item>
        <el-form-item label="结算金额">
          <el-input :value="'¥' + formatMoney(settleForm.applyAmount)" disabled />
        </el-form-item>
        <el-form-item label="支付渠道" prop="payChannel">
          <el-input v-model="settleForm.payChannel" placeholder="请输入支付渠道" />
        </el-form-item>
        <el-form-item label="外部交易单号" prop="tradeNo">
          <el-input v-model="settleForm.tradeNo" placeholder="请输入外部交易单号，可为空" />
        </el-form-item>
        <el-form-item label="结算备注" prop="remark">
          <el-input
            v-model="settleForm.remark"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入结算备注"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="settleOpen = false">取消</el-button>
        <el-button type="primary" :loading="settleSubmitting" @click="submitSettle">
          确认结算
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSettlementManage,
  listSettlementManage,
  settleSettlementManage
} from '@/api/school/settlementManage'
import { getFinanceFlow } from '@/api/school/financeFlow'

export default {
  name: 'SettlementManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      detailOpen: false,
      settleOpen: false,
      settleSubmitting: false,
      total: 0,
      dateRange: [],
      settlementManageList: [],
      detail: {},
      financeFlow: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: undefined,
        supplierId: undefined,
        supplierName: undefined,
        status: undefined
      },
      settleForm: {
        id: undefined,
        withdrawNo: '',
        supplierName: '',
        applyAmount: 0,
        payChannel: '线下转账',
        tradeNo: '',
        remark: ''
      },
      settleRules: {
        payChannel: [
          { required: true, message: '请输入支付渠道', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    pageSummary() {
      const rows = this.settlementManageList || []
      const pendingRows = rows.filter((item) => String(item.status) === '1')
      const settledRows = rows.filter((item) => String(item.status) === '3')
      return {
        totalAmount: this.sumAmount(rows, 'applyAmount'),
        pendingAmount: this.sumAmount(pendingRows, 'applyAmount'),
        pendingCount: pendingRows.length,
        settledAmount: this.sumAmount(settledRows, 'applyAmount'),
        settledCount: settledRows.length,
        flowCount: rows.filter((item) => item.financeFlowId).length
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSettlementManage(this.addDateRange({ ...this.queryParams }, this.dateRange)).then((response) => {
        this.settlementManageList = response.rows || []
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
      this.financeFlow = {}
      getSettlementManage(row.id).then((response) => {
        this.detail = response.data || {}
        this.detailOpen = true
        if (this.detail.financeFlowId) {
          return getFinanceFlow(this.detail.financeFlowId)
        }
        return null
      }).then((response) => {
        if (response) {
          this.financeFlow = response.data || {}
        }
      })
    },
    openSettle(row) {
      this.settleForm = {
        id: row.id,
        withdrawNo: row.withdrawNo,
        supplierName: row.supplierName,
        applyAmount: row.applyAmount,
        payChannel: '线下转账',
        tradeNo: '',
        remark: ''
      }
      this.settleOpen = true
      this.$nextTick(() => {
        if (this.$refs.settleForm) {
          this.$refs.settleForm.clearValidate()
        }
      })
    },
    submitSettle() {
      this.$refs.settleForm.validate((valid) => {
        if (!valid) {
          return
        }
        this.$modal.confirm('确认已完成线下打款并执行结算吗？').then(() => {
          this.settleSubmitting = true
          return settleSettlementManage({
            id: this.settleForm.id,
            payChannel: this.settleForm.payChannel,
            tradeNo: this.settleForm.tradeNo,
            remark: this.settleForm.remark
          })
        }).then(() => {
          this.$modal.msgSuccess('结算成功')
          this.settleOpen = false
          this.getList()
        }).catch(() => {
        }).finally(() => {
          this.settleSubmitting = false
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
    formatStatus(value) {
      const textMap = {
        '1': '待结算',
        '3': '已结算'
      }
      return textMap[String(value)] || value || '-'
    },
    statusTagType(value) {
      const typeMap = {
        '1': 'warning',
        '3': 'success'
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

.summary-card.settled {
  background: linear-gradient(135deg, #22c55e, #15803d);
}

.summary-card.flow {
  background: linear-gradient(135deg, #64748b, #334155);
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

.json-preview {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: Consolas, Monaco, monospace;
}
</style>
