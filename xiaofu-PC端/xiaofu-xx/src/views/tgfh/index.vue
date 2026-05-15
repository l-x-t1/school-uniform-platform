<template>
  <div class="app-container">
    <el-alert
      title="此页面展示当前学校相关发货单统计，可按发货状态和厂商名称查询。"
      type="warning"
      :closable="false"
      show-icon
      class="alert-warning mb15"
    />

    <el-form :inline="true" :model="queryParams">
      <el-form-item label="发货状态：">
        <el-select v-model="queryParams.deliveryStatus" placeholder="请选择" clearable style="width: 180px;">
          <el-option label="全部" value="" />
          <el-option label="待发货" value="0" />
          <el-option label="已发货" value="1" />
          <el-option label="已签收" value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.supplierName" placeholder="请输入厂商名称" clearable style="width: 220px;" @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="发货单号：">
        <el-input v-model="queryParams.deliveryNo" placeholder="请输入发货单号" clearable style="width: 200px;" @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="订单号：">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable style="width: 200px;" @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="发货单号" prop="deliveryNo" min-width="170" :show-overflow-tooltip="true" />
      <el-table-column label="订单号" prop="orderNo" min-width="170" :show-overflow-tooltip="true" />
      <el-table-column label="学校名称" prop="schoolName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="厂商名称" prop="supplierName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="发货数量" prop="deliveryTotalNum" width="100" align="center" />
      <el-table-column label="签收数量" prop="signTotalNum" width="100" align="center" />
      <el-table-column label="运费" prop="carriageFee" width="100" align="center" />
      <el-table-column label="发货时间" prop="deliveryTime" width="170" align="center">
        <template slot-scope="scope">
          <span>{{ formatTime(scope.row.deliveryTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签收时间" prop="signTime" width="170" align="center">
        <template slot-scope="scope">
          <span>{{ formatTime(scope.row.signTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="deliveryStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.deliveryStatus)">
            {{ statusText(scope.row.deliveryStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
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

    <el-dialog title="发货单详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发货单号">{{ formatValue(detailInfo.deliveryNo) }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ formatValue(detailInfo.orderNo) }}</el-descriptions-item>
        <el-descriptions-item label="学校名称">{{ formatValue(detailInfo.schoolName) }}</el-descriptions-item>
        <el-descriptions-item label="厂商名称">{{ formatValue(detailInfo.supplierName) }}</el-descriptions-item>
        <el-descriptions-item label="发货状态">{{ statusText(detailInfo.deliveryStatus) }}</el-descriptions-item>
        <el-descriptions-item label="发货数量">{{ formatValue(detailInfo.deliveryTotalNum) }}</el-descriptions-item>
        <el-descriptions-item label="签收数量">{{ formatValue(detailInfo.signTotalNum) }}</el-descriptions-item>
        <el-descriptions-item label="运费">{{ formatValue(detailInfo.carriageFee) }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ formatTime(detailInfo.deliveryTime) }}</el-descriptions-item>
        <el-descriptions-item label="签收时间">{{ formatTime(detailInfo.signTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="detailInfo.xfptDeliveryOrderItemList || []" border style="width: 100%; margin-top: 16px;">
        <el-table-column label="商品名称" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="尺码" prop="sizeName" width="120" align="center" />
        <el-table-column label="发货数量" prop="deliveryNum" width="120" align="center" />
        <el-table-column label="签收数量" prop="signNum" width="120" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ statusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listDeliveryStatistics } from '@/api/school/deliveryStatistics'
import { getDeliveryStatistics } from '@/api/tonggou/tgfh'

export default {
  name: 'TgfhIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      detailOpen: false,
      detailInfo: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deliveryStatus: '',
        supplierName: '',
        deliveryNo: '',
        orderNo: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDeliveryStatistics(this.queryParams).then(response => {
        this.tableData = response.rows || []
        this.total = response.total || 0
      }).catch(() => {
        this.tableData = []
        this.total = 0
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
        deliveryStatus: '',
        supplierName: '',
        deliveryNo: '',
        orderNo: ''
      }
      this.getList()
    },
    handleView(row) {
      this.detailOpen = true
      this.detailInfo = row
      getDeliveryStatistics(row.id).then(response => {
        this.detailInfo = {
          ...row,
          ...(response.data || {})
        }
      })
    },
    statusText(status) {
      const textMap = {
        '0': '待发货',
        '1': '已发货',
        '2': '已签收'
      }
      return textMap[String(status)] || '-'
    },
    statusTagType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'success'
      }
      return typeMap[String(status)] || 'info'
    },
    formatValue(value) {
      return value === null || value === undefined || value === '' ? '-' : value
    },
    formatTime(value) {
      if (!value) return '-'
      return this.parseTime(value, '{y}-{m}-{d}') || value
    }
  }
}
</script>

<style scoped>
.alert-warning {
  margin-bottom: 15px;
  background-color: #fdf6ec;
  color: #e6a23c;
}

.mb15 {
  margin-bottom: 15px;
}
</style>
