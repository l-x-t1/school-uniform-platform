<template>
  <div class="app-container daily-income-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card primary">
          <div class="summary-label">当前页入账总额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.totalAmount) }}</div>
          <div class="summary-extra">入账笔数 {{ pageSummary.totalCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card school">
          <div class="summary-label">学校入账金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.schoolAmount) }}</div>
          <div class="summary-extra">状态：已到学校账户</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card supplier">
          <div class="summary-label">供应商入账金额</div>
          <div class="summary-value">¥{{ formatMoney(pageSummary.supplierAmount) }}</div>
          <div class="summary-extra">状态：已到供应商账户</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card days">
          <div class="summary-label">当前页入账天数</div>
          <div class="summary-value">{{ dailyIncomeList.length }}</div>
          <div class="summary-extra">按交易日期聚合</div>
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
      <el-form-item label="入账日期">
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
      <el-form-item label="收款方类型" prop="payeeType">
        <el-select v-model="queryParams.payeeType" placeholder="请选择" clearable>
          <el-option label="学校" value="1" />
          <el-option label="供应商" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="学校ID" prop="schoolId">
        <el-input
          v-model="queryParams.schoolId"
          placeholder="请输入学校ID"
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
      <el-form-item label="流水单号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      title="统计口径：收入方向、已到账资金流水；未支付和支出流水不计入。"
      type="info"
      :closable="false"
      show-icon
      class="rule-alert"
    />

    <el-table v-loading="loading" :data="dailyIncomeList" border>
      <el-table-column label="入账日期" prop="incomeDate" width="140" align="center" />
      <el-table-column label="入账总额" width="140" align="right">
        <template slot-scope="scope">
          ¥{{ formatMoney(scope.row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="入账笔数" prop="totalCount" width="100" align="center" />
      <el-table-column label="学校入账金额" width="150" align="right">
        <template slot-scope="scope">
          ¥{{ formatMoney(scope.row.schoolAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="供应商入账金额" width="160" align="right">
        <template slot-scope="scope">
          ¥{{ formatMoney(scope.row.supplierAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="180">
        <template slot-scope="scope">
          {{ buildIncomeRemark(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:dailyIncome:query']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="openDetail(scope.row)"
          >
            详情
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

    <el-dialog :title="detailTitle" :visible.sync="detailOpen" width="980px" append-to-body>
      <el-table v-loading="detailLoading" :data="detailList" border>
        <el-table-column label="流水单号" prop="flowNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="业务类型" width="100" align="center">
          <template slot-scope="scope">
            {{ formatBusinessType(scope.row.businessType) }}
          </template>
        </el-table-column>
        <el-table-column label="订单号" prop="orderNo" min-width="170" show-overflow-tooltip />
        <el-table-column label="收款方" prop="payeeName" min-width="150" show-overflow-tooltip />
        <el-table-column label="金额" width="120" align="right">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column label="到账状态" width="140" align="center">
          <template slot-scope="scope">
            {{ formatFlowStatus(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="交易时间" width="160" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.tradeTime) || "-" }}
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="detailTotal > 0"
        :total="detailTotal"
        :page.sync="detailQueryParams.pageNum"
        :limit.sync="detailQueryParams.pageSize"
        @pagination="getDetailList"
      />

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDailyIncome, listDailyIncomeDetail } from '@/api/school/financeFlow'

export default {
  name: 'DailyIncome',
  data() {
    return {
      loading: false,
      detailLoading: false,
      showSearch: true,
      total: 0,
      detailTotal: 0,
      dateRange: [],
      dailyIncomeList: [],
      detailList: [],
      detailOpen: false,
      detailTitle: '入账详情',
      currentIncomeDate: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        payeeType: undefined,
        schoolId: undefined,
        supplierId: undefined,
        flowNo: undefined
      },
      detailQueryParams: {
        pageNum: 1,
        pageSize: 10,
        tradeTime: undefined,
        payeeType: undefined,
        schoolId: undefined,
        supplierId: undefined,
        flowNo: undefined
      }
    }
  },
  computed: {
    pageSummary() {
      return this.dailyIncomeList.reduce((summary, item) => {
        summary.totalAmount += this.toNumber(item.totalAmount)
        summary.totalCount += this.toNumber(item.totalCount)
        summary.schoolAmount += this.toNumber(item.schoolAmount)
        summary.supplierAmount += this.toNumber(item.supplierAmount)
        return summary
      }, {
        totalAmount: 0,
        totalCount: 0,
        schoolAmount: 0,
        supplierAmount: 0
      })
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDailyIncome(this.addDateRange({ ...this.queryParams }, this.dateRange)).then((response) => {
        this.dailyIncomeList = response.rows || []
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
      this.currentIncomeDate = row.incomeDate
      this.detailTitle = `${row.incomeDate || '-'} 入账详情`
      this.detailQueryParams = {
        pageNum: 1,
        pageSize: 10,
        tradeTime: row.incomeDate,
        payeeType: this.queryParams.payeeType,
        schoolId: this.queryParams.schoolId,
        supplierId: this.queryParams.supplierId,
        flowNo: this.queryParams.flowNo
      }
      this.detailOpen = true
      this.getDetailList()
    },
    getDetailList() {
      if (!this.detailQueryParams.tradeTime) {
        this.detailList = []
        this.detailTotal = 0
        return
      }
      this.detailLoading = true
      listDailyIncomeDetail(this.detailQueryParams).then((response) => {
        this.detailList = response.rows || []
        this.detailTotal = response.total || 0
      }).finally(() => {
        this.detailLoading = false
      })
    },
    buildIncomeRemark(row) {
      const totalCount = this.toNumber(row.totalCount)
      return `共${totalCount}笔，学校入账${this.formatMoney(row.schoolAmount)}，供应商入账${this.formatMoney(row.supplierAmount)}`
    },
    formatMoney(value) {
      return this.toNumber(value).toFixed(2)
    },
    toNumber(value) {
      const numberValue = Number(value || 0)
      return Number.isNaN(numberValue) ? 0 : numberValue
    },
    formatBusinessType(value) {
      const textMap = {
        '1': '支付',
        '2': '退款',
        '3': '提现',
        '4': '结算'
      }
      return textMap[String(value)] || value || '-'
    },
    formatFlowStatus(value) {
      const textMap = {
        '1': '已到学校账户',
        '2': '已到供应商账户'
      }
      return textMap[String(value)] || value || '-'
    }
  }
}
</script>

<style scoped>
.summary-row {
  margin-bottom: 18px;
}

.summary-card {
  border-radius: 12px;
  color: #fff;
  margin-bottom: 16px;
  min-height: 118px;
  padding: 18px 20px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.summary-card.primary {
  background: linear-gradient(135deg, #2563eb, #0f766e);
}

.summary-card.school {
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.summary-card.supplier {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.summary-card.days {
  background: linear-gradient(135deg, #64748b, #334155);
}

.summary-label {
  font-size: 14px;
  opacity: 0.86;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 42px;
}

.summary-extra {
  font-size: 13px;
  opacity: 0.78;
}

.rule-alert {
  margin-bottom: 12px;
}
</style>
