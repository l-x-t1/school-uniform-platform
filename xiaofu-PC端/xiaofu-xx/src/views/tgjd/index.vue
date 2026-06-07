<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams" label-width="90px">
      <el-form-item label="班级名称">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入班级名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.progressStatus" placeholder="请选择状态" clearable style="width: 180px">
          <el-option label="待查看" value="0" />
          <el-option label="供应商已查看" value="1" />
          <el-option label="制作中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已发货" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="订购单号">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订购单号"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border style="width: 100%">
      <el-table-column label="学校名称" prop="schoolName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="班级名称" prop="className" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="订购单号" prop="orderNo" min-width="170" :show-overflow-tooltip="true" />
      <el-table-column label="订购数量" prop="orderQuantity" align="center" width="100" />
      <el-table-column label="状态" prop="progressStatusName" align="center" width="130">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.progressStatus)">{{ scope.row.progressStatusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生产单号" prop="productionNo" min-width="170" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.productionNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发货单号" prop="deliveryNo" min-width="170" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.deliveryNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="物流单号" prop="logisticsNo" min-width="150" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.logisticsNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" min-width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">查看</el-button>
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

    <el-dialog title="订购明细" :visible.sync="detailVisible" width="900px" append-to-body>
      <el-descriptions :column="2" border class="order-summary">
        <el-descriptions-item label="学校名称">{{ currentRow.schoolName }}</el-descriptions-item>
        <el-descriptions-item label="班级名称">{{ currentRow.className }}</el-descriptions-item>
        <el-descriptions-item label="订购单号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentRow.progressStatusName }}</el-descriptions-item>
        <el-descriptions-item label="生产单号">{{ currentRow.productionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货单号">{{ currentRow.deliveryNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ currentRow.logisticsNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailList" border style="width: 100%; margin-top: 16px">
        <el-table-column label="商品名称" prop="productName" :show-overflow-tooltip="true" />
        <el-table-column label="商品单价" prop="price" width="120" align="center" />
        <el-table-column label="订购数量" prop="quantity" width="120" align="center" />
        <el-table-column label="订购人" prop="orderPerson" width="140" align="center" />
        <el-table-column label="订购时间" prop="orderTime" width="180" align="center" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listGroupNoticeProgress,
  getGroupNoticeProgressDetail
} from '@/api/school/groupNotice'

export default {
  name: 'TgjdIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        className: '',
        progressStatus: '',
        orderNo: ''
      },
      detailVisible: false,
      detailList: [],
      currentRow: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listGroupNoticeProgress(this.queryParams).then(response => {
        this.tableData = response.rows || []
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
      this.queryParams.className = ''
      this.queryParams.progressStatus = ''
      this.queryParams.orderNo = ''
      this.handleQuery()
    },
    handleDetail(row) {
      if (!row || !row.id) {
        this.$modal.msgError('当前订单缺少主键，无法查看详情')
        return
      }
      this.currentRow = row
      this.detailVisible = true
      this.detailList = []
      getGroupNoticeProgressDetail(row.id, row.supplierId).then(response => {
        this.detailList = response.data || []
      })
    },
    statusTagType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'info',
        '2': 'warning',
        '3': 'success',
        '4': 'primary'
      }
      return typeMap[String(status || '0')] || ''
    }
  }
}
</script>

<style scoped>
.order-summary {
  margin-bottom: 8px;
}
</style>
