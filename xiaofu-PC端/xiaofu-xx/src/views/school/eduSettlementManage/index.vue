<template>
  <div class="app-container edu-settlement-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card total">
          <div class="summary-label">结算金额</div>
          <div class="summary-value">{{ formatMoney(statistics.settlementAmount) }}</div>
          <div class="summary-extra">结算记录 {{ statistics.totalCount || 0 }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card pending">
          <div class="summary-label">待结算金额</div>
          <div class="summary-value">{{ formatMoney(pendingAmount) }}</div>
          <div class="summary-extra">待结算 {{ statistics.pendingCount || 0 }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card paid">
          <div class="summary-label">已付金额</div>
          <div class="summary-value">{{ formatMoney(statistics.paidAmount) }}</div>
          <div class="summary-extra">已结算 {{ statistics.settledCount || 0 }} 笔</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card unpaid">
          <div class="summary-label">未付金额</div>
          <div class="summary-value">{{ formatMoney(statistics.unpaidAmount) }}</div>
          <div class="summary-extra">部分结算 {{ statistics.partialCount || 0 }} 笔</div>
        </div>
      </el-col>
    </el-row>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="82px"
    >
      <el-form-item label="结算单位" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入结算单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项目名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结算周期" prop="settlementCycle">
        <el-date-picker
          v-model="queryParams.settlementCycle"
          type="month"
          value-format="yyyy-MM"
          placeholder="请选择结算周期"
          clearable
        />
      </el-form-item>
      <el-form-item label="结算状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="待结算" value="0" />
          <el-option label="部分结算" value="1" />
          <el-option label="已结算" value="2" />
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
      title="结算说明：教育局结算管理为独立只读台账，用于按单位、项目、周期、状态查询结算金额、税额、已付和未付金额。"
      type="info"
      :closable="false"
      show-icon
      class="rule-alert"
    />

    <el-table v-loading="loading" :data="settlementList" border highlight-current-row>
      <el-table-column label="结算单位" prop="unitName" min-width="170" show-overflow-tooltip />
      <el-table-column label="项目名称" prop="projectName" min-width="190" show-overflow-tooltip />
      <el-table-column label="结算周期" prop="settlementCycle" width="120" align="center" />
      <el-table-column label="结算状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结算金额" width="130" align="right">
        <template slot-scope="scope">
          {{ formatMoney(scope.row.settlementAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="税额" width="110" align="right">
        <template slot-scope="scope">
          {{ formatMoney(scope.row.taxAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="已付金额" width="130" align="right">
        <template slot-scope="scope">
          {{ formatMoney(scope.row.paidAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="未付金额" width="130" align="right">
        <template slot-scope="scope">
          {{ formatMoney(scope.row.unpaidAmount) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-hasPermi="['school:eduSettlementManage:query']"
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

    <el-dialog title="教育局结算详情" :visible.sync="detailOpen" width="820px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="结算单位">{{ detail.unitName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detail.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结算周期">{{ detail.settlementCycle || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ formatStatus(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="结算金额">{{ formatMoney(detail.settlementAmount) }}</el-descriptions-item>
        <el-descriptions-item label="税额">{{ formatMoney(detail.taxAmount) }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">{{ formatMoney(detail.paidAmount) }}</el-descriptions-item>
        <el-descriptions-item label="未付金额">{{ formatMoney(detail.unpaidAmount) }}</el-descriptions-item>
        <el-descriptions-item label="结算说明" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getEduSettlementManage,
  getEduSettlementStatistics,
  listEduSettlementManage
} from '@/api/school/eduSettlementManage'

export default {
  name: 'EduSettlementManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      detailOpen: false,
      total: 0,
      settlementList: [],
      detail: {},
      statistics: {
        settlementAmount: 0,
        taxAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0,
        totalCount: 0,
        pendingCount: 0,
        partialCount: 0,
        settledCount: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        unitName: undefined,
        projectName: undefined,
        settlementCycle: undefined,
        status: undefined
      }
    }
  },
  computed: {
    pendingAmount() {
      return Number(this.statistics.unpaidAmount || 0)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      return Promise.all([
        listEduSettlementManage(params),
        getEduSettlementStatistics(params)
      ]).then(([listResponse, statisticsResponse]) => {
        this.settlementList = listResponse.rows || []
        this.total = listResponse.total || 0
        this.statistics = {
          ...this.statistics,
          ...(statisticsResponse.data || {})
        }
      }).finally(() => {
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
    openDetail(row) {
      getEduSettlementManage(row.id).then((response) => {
        this.detail = response.data || row
        this.detailOpen = true
      })
    },
    formatMoney(value) {
      return `¥${Number(value || 0).toFixed(2)}`
    },
    formatStatus(value) {
      const statusMap = {
        '0': '待结算',
        '1': '部分结算',
        '2': '已结算'
      }
      return statusMap[String(value)] || '-'
    },
    statusTagType(value) {
      const typeMap = {
        '0': 'warning',
        '1': '',
        '2': 'success'
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
  min-height: 150px;
  border-radius: 10px;
  color: #fff;
  padding: 24px 26px;
  margin-bottom: 16px;
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.08);
}

.summary-card.total {
  background: linear-gradient(135deg, #2563eb 0%, #0f766e 100%);
}

.summary-card.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #ea580c 100%);
}

.summary-card.paid {
  background: linear-gradient(135deg, #22c55e 0%, #15803d 100%);
}

.summary-card.unpaid {
  background: linear-gradient(135deg, #64748b 0%, #334155 100%);
}

.summary-label {
  font-size: 15px;
  opacity: 0.94;
}

.summary-value {
  margin-top: 14px;
  font-size: 34px;
  line-height: 42px;
  font-weight: 700;
}

.summary-extra {
  margin-top: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.rule-alert {
  margin-bottom: 16px;
}
</style>
