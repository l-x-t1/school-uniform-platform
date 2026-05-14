<template>
  <div class="app-container edu-settlement-page">
    <el-card class="search-card" shadow="never">
      <el-form
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
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="待结算" value="0" />
            <el-option label="部分结算" value="1" />
            <el-option label="已结算" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="content-card" shadow="never">
      <div slot="header" class="card-header">
        <div>
          <div class="page-title">教育局结算管理</div>
          <div class="page-desc">按单位、项目、周期、状态查询结算金额、税额、已付和未付金额</div>
        </div>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="handleQuery" />
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="列表" name="list">
          <el-table
            v-loading="loading"
            :data="filteredList"
            border
            highlight-current-row
            @current-change="handleCurrentChange"
          >
            <el-table-column label="结算单位" prop="unitName" min-width="160" show-overflow-tooltip />
            <el-table-column label="项目名称" prop="projectName" min-width="180" show-overflow-tooltip />
            <el-table-column label="结算周期" prop="settlementCycle" width="120" align="center" />
            <el-table-column label="状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="statusTagType(scope.row.status)">
                  {{ formatStatus(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="结算金额" width="130" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.settlementAmount) }}</template>
            </el-table-column>
            <el-table-column label="税额" width="110" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.taxAmount) }}</template>
            </el-table-column>
            <el-table-column label="已付金额" width="130" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.paidAmount) }}</template>
            </el-table-column>
            <el-table-column label="未付金额" width="130" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.unpaidAmount) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" icon="el-icon-view" @click="openDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="详情" name="detail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="结算单位">{{ currentRow.unitName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="项目名称">{{ currentRow.projectName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="结算周期">{{ currentRow.settlementCycle || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ formatStatus(currentRow.status) }}</el-descriptions-item>
            <el-descriptions-item label="结算金额">{{ formatMoney(currentRow.settlementAmount) }}</el-descriptions-item>
            <el-descriptions-item label="税额">{{ formatMoney(currentRow.taxAmount) }}</el-descriptions-item>
            <el-descriptions-item label="已付金额">{{ formatMoney(currentRow.paidAmount) }}</el-descriptions-item>
            <el-descriptions-item label="未付金额">{{ formatMoney(currentRow.unpaidAmount) }}</el-descriptions-item>
            <el-descriptions-item label="结算说明" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="结算统计" name="statistics">
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12" :lg="6">
              <div class="summary-card amount">
                <div class="summary-label">结算金额</div>
                <div class="summary-value">{{ formatMoney(summary.settlementAmount) }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <div class="summary-card tax">
                <div class="summary-label">税额</div>
                <div class="summary-value">{{ formatMoney(summary.taxAmount) }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <div class="summary-card paid">
                <div class="summary-label">已付金额</div>
                <div class="summary-value">{{ formatMoney(summary.paidAmount) }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <div class="summary-card unpaid">
                <div class="summary-label">未付金额</div>
                <div class="summary-value">{{ formatMoney(summary.unpaidAmount) }}</div>
              </div>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EduSettlementManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      activeTab: 'list',
      currentRow: {},
      queryParams: {
        unitName: undefined,
        projectName: undefined,
        settlementCycle: undefined,
        status: undefined
      },
      settlementList: [
        {
          id: 1,
          unitName: '石家庄市教育局',
          projectName: '2026年春季校服统购项目',
          settlementCycle: '2026-05',
          status: '0',
          settlementAmount: 128600.00,
          taxAmount: 7716.00,
          paidAmount: 0,
          unpaidAmount: 128600.00,
          remark: '按市级统购项目汇总结算，当前等待财务确认付款。'
        },
        {
          id: 2,
          unitName: '长安区教育局',
          projectName: '小学夏季校服采购项目',
          settlementCycle: '2026-05',
          status: '1',
          settlementAmount: 86520.00,
          taxAmount: 5191.20,
          paidAmount: 40000.00,
          unpaidAmount: 46520.00,
          remark: '已完成首批付款，剩余金额待到货验收后结算。'
        },
        {
          id: 3,
          unitName: '桥西区教育局',
          projectName: '中学校服补充采购项目',
          settlementCycle: '2026-04',
          status: '2',
          settlementAmount: 73280.00,
          taxAmount: 4396.80,
          paidAmount: 73280.00,
          unpaidAmount: 0,
          remark: '项目已完成结算，资金流水和付款凭证已归档。'
        },
        {
          id: 4,
          unitName: '新华区教育局',
          projectName: '校服质量监管抽检项目',
          settlementCycle: '2026-05',
          status: '0',
          settlementAmount: 32800.00,
          taxAmount: 1968.00,
          paidAmount: 0,
          unpaidAmount: 32800.00,
          remark: '抽检服务费用待审核后结算。'
        },
        {
          id: 5,
          unitName: '裕华区教育局',
          projectName: '秋季运动校服采购项目',
          settlementCycle: '2026-06',
          status: '1',
          settlementAmount: 154900.00,
          taxAmount: 9294.00,
          paidAmount: 90000.00,
          unpaidAmount: 64900.00,
          remark: '按合同周期分批结算，第二批付款待确认。'
        }
      ]
    }
  },
  computed: {
    filteredList() {
      return this.settlementList.filter((item) => {
        const unitMatched = !this.queryParams.unitName || item.unitName.includes(this.queryParams.unitName)
        const projectMatched = !this.queryParams.projectName || item.projectName.includes(this.queryParams.projectName)
        const cycleMatched = !this.queryParams.settlementCycle || item.settlementCycle === this.queryParams.settlementCycle
        const statusMatched = this.queryParams.status === undefined || this.queryParams.status === '' || item.status === this.queryParams.status
        return unitMatched && projectMatched && cycleMatched && statusMatched
      })
    },
    summary() {
      return this.filteredList.reduce((total, item) => {
        total.settlementAmount += Number(item.settlementAmount || 0)
        total.taxAmount += Number(item.taxAmount || 0)
        total.paidAmount += Number(item.paidAmount || 0)
        total.unpaidAmount += Number(item.unpaidAmount || 0)
        return total
      }, {
        settlementAmount: 0,
        taxAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0
      })
    }
  },
  methods: {
    handleQuery() {
      this.activeTab = 'list'
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleCurrentChange(row) {
      if (row) {
        this.currentRow = row
      }
    },
    openDetail(row) {
      this.currentRow = row
      this.activeTab = 'detail'
    },
    formatMoney(value) {
      return `￥${Number(value || 0).toFixed(2)}`
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
        '1': 'primary',
        '2': 'success'
      }
      return typeMap[String(value)] || 'info'
    }
  }
}
</script>

<style scoped>
.edu-settlement-page .search-card,
.edu-settlement-page .content-card {
  border: 1px solid #e6ebf5;
  border-radius: 4px;
}

.edu-settlement-page .search-card {
  margin-bottom: 18px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.page-title {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  line-height: 26px;
}

.page-desc {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.summary-card {
  min-height: 108px;
  border-radius: 6px;
  padding: 18px 20px;
  color: #fff;
  margin-bottom: 16px;
}

.summary-card.amount {
  background: #2563eb;
}

.summary-card.tax {
  background: #0f766e;
}

.summary-card.paid {
  background: #16a34a;
}

.summary-card.unpaid {
  background: #d97706;
}

.summary-label {
  font-size: 14px;
  opacity: 0.92;
}

.summary-value {
  margin-top: 14px;
  font-size: 26px;
  font-weight: 700;
}
</style>
