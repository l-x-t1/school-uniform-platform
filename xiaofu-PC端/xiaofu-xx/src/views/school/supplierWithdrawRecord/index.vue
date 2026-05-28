<template>
  <div class="app-container supplier-withdraw-record-page">
    <div class="page-hero">
      <div>
        <div class="page-title">供应商提现记录</div>
        <div class="page-subtitle">展示供应商提现申请的列表与详情，不包含新增、修改、删除操作。</div>
      </div>
    </div>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card blue">
          <div class="summary-label">当前页申请总额</div>
          <div class="summary-value">¥{{ amountText(summary.totalAmount) }}</div>
          <div class="summary-extra">记录数 {{ withdrawApplyList.length }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card orange">
          <div class="summary-label">待审核</div>
          <div class="summary-value">{{ summary.pendingCount }}</div>
          <div class="summary-extra">金额 ¥{{ amountText(summary.pendingAmount) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card green">
          <div class="summary-label">已打款</div>
          <div class="summary-value">{{ summary.paidCount }}</div>
          <div class="summary-extra">金额 ¥{{ amountText(summary.paidAmount) }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card red">
          <div class="summary-label">已拒绝/已取消</div>
          <div class="summary-value">{{ summary.closedCount }}</div>
          <div class="summary-extra">已结束记录</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="90px">
        <el-form-item label="提现单号" prop="withdrawNo">
          <el-input
            v-model="queryParams.withdrawNo"
            placeholder="请输入提现单号"
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
        <el-form-item label="申请日期" prop="applyTime">
          <el-date-picker
            v-model="queryParams.applyTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择申请日期"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="withdrawApplyList" border>
        <el-table-column label="提现单号" prop="withdrawNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="供应商ID" prop="supplierId" width="100" />
        <el-table-column label="供应商名称" prop="supplierName" min-width="140" show-overflow-tooltip />
        <el-table-column label="申请金额" width="120">
          <template slot-scope="scope">¥{{ amountText(scope.row.applyAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">
              {{ withdrawStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="120">
          <template slot-scope="scope">{{ parseTime(scope.row.applyTime, "{y}-{m}-{d}") }}</template>
        </el-table-column>
        <el-table-column label="审核人" prop="auditUserName" min-width="120" show-overflow-tooltip />
        <el-table-column label="打款时间" width="170">
          <template slot-scope="scope">{{ parseTime(scope.row.payTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</template>
        </el-table-column>
        <el-table-column label="关联流水ID" prop="financeFlowId" width="120" />
        <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
        <el-table-column label="详情" width="90" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="showDetail(scope.row)">查看</el-button>
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
    </el-card>

    <el-drawer
      title="提现详情"
      :visible.sync="detailOpen"
      size="520px"
      append-to-body
    >
      <div v-if="detailData" class="detail-panel">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="提现单号">{{ detailData.withdrawNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="供应商ID">{{ detailData.supplierId || "-" }}</el-descriptions-item>
          <el-descriptions-item label="供应商名称">{{ detailData.supplierName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">¥{{ amountText(detailData.applyAmount) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ withdrawStatusText(detailData.status) }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ parseTime(detailData.applyTime, "{y}-{m}-{d}") || "-" }}</el-descriptions-item>
          <el-descriptions-item label="审核人ID">{{ detailData.auditUserId || "-" }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ detailData.auditUserName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ parseTime(detailData.auditTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
          <el-descriptions-item label="打款时间">{{ parseTime(detailData.payTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
          <el-descriptions-item label="关联流水ID">{{ detailData.financeFlowId || "-" }}</el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ detailData.auditRemark || "-" }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || "-" }}</el-descriptions-item>
        </el-descriptions>

        <div class="bank-card">
          <div class="bank-title">收款信息快照</div>
          <pre class="bank-json">{{ bankInfoPreview }}</pre>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getWithdrawApply, listWithdrawApply } from '@/api/school/withdrawApply'

export default {
  name: 'SupplierWithdrawRecord',
  data() {
    return {
      loading: false,
      total: 0,
      detailOpen: false,
      withdrawApplyList: [],
      detailData: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        withdrawNo: undefined,
        supplierId: undefined,
        supplierName: undefined,
        status: undefined,
        applyTime: undefined
      },
      statusOptions: [
        { label: '待审核', value: '0' },
        { label: '审核通过', value: '1' },
        { label: '审核拒绝', value: '2' },
        { label: '已打款', value: '3' },
        { label: '已取消', value: '4' }
      ]
    }
  },
  computed: {
    summary() {
      const rows = this.withdrawApplyList || []
      const pendingRows = rows.filter((item) => String(item.status) === '0')
      const paidRows = rows.filter((item) => String(item.status) === '3')
      const closedRows = rows.filter((item) => ['2', '4'].includes(String(item.status)))
      return {
        totalAmount: this.sumAmount(rows),
        pendingCount: pendingRows.length,
        pendingAmount: this.sumAmount(pendingRows),
        paidCount: paidRows.length,
        paidAmount: this.sumAmount(paidRows),
        closedCount: closedRows.length
      }
    },
    bankInfoPreview() {
      if (!this.detailData || !this.detailData.bankInfoJson) {
        return '暂无收款信息'
      }
      const bankInfo = this.parseBankInfo(this.detailData.bankInfoJson)
      return typeof bankInfo === 'string' ? bankInfo : JSON.stringify(bankInfo, null, 2)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listWithdrawApply(this.queryParams)
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
      this.handleQuery()
    },
    showDetail(row) {
      const id = row && row.id
      if (!id) {
        this.detailData = row || {}
        this.detailOpen = true
        return
      }
      getWithdrawApply(id)
        .then((response) => {
          this.detailData = response.data || row
          this.detailOpen = true
        })
        .catch(() => {
          this.detailData = row || {}
          this.detailOpen = true
          this.$message.warning('详情接口返回异常，已展示列表中的记录信息')
        })
    },
    withdrawStatusText(value) {
      const map = {
        0: '待审核',
        1: '审核通过',
        2: '审核拒绝',
        3: '已打款',
        4: '已取消'
      }
      return map[String(value)] || '-'
    },
    statusTagType(value) {
      const map = {
        0: 'warning',
        1: 'success',
        2: 'danger',
        3: '',
        4: 'info'
      }
      return map[String(value)] || 'info'
    },
    amountText(value) {
      return Number(value || 0).toFixed(2)
    },
    sumAmount(list) {
      return (list || []).reduce((sum, item) => sum + Number(item.applyAmount || 0), 0)
    },
    parseBankInfo(value) {
      if (!value) {
        return ''
      }
      if (typeof value !== 'string') {
        return value
      }
      try {
        return JSON.parse(value)
      } catch (error) {
        return value
      }
    }
  }
}
</script>

<style scoped>
.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 24px 26px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eff6ff, #f0fdf4);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
}

.page-subtitle {
  margin-top: 8px;
  color: #475569;
  font-size: 14px;
}

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

.summary-card.blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.summary-card.orange {
  background: linear-gradient(135deg, #f59e0b, #ea580c);
}

.summary-card.green {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.summary-card.red {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.summary-label {
  font-size: 14px;
  opacity: 0.92;
}

.summary-value {
  margin: 10px 0 6px;
  font-size: 28px;
  font-weight: 700;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.9;
}

.detail-panel {
  padding: 0 16px 16px;
}

.bank-card {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.bank-title {
  padding: 12px 14px;
  background: #f8fafc;
  color: #0f172a;
  font-weight: 600;
}

.bank-json {
  margin: 0;
  padding: 14px;
  background: #0f172a;
  color: #e2e8f0;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
