<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="发货状态：">
        <el-select v-model="queryParams.deliveryStatus" placeholder="全部" style="width: 180px;">
          <el-option label="全部" value="" />
          <el-option label="待发货" value="0" />
          <el-option label="已发货" value="1" />
          <el-option label="已签收" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="发货单号：">
        <el-input v-model="queryParams.deliveryNo" placeholder="请输入发货单号" clearable style="width: 200px;" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号：">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable style="width: 200px;" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="厂商ID：">
        <el-input v-model="queryParams.supplierId" placeholder="请输入厂商ID" clearable style="width: 160px;" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="发货单号" prop="deliveryNo" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="统购公告ID" prop="noticeId" width="120" align="center" />
      <el-table-column label="订单号" prop="orderNo" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="厂商ID" prop="supplierId" width="110" align="center" />
      <el-table-column label="收货人" prop="receiverName" width="120" align="center" />
      <el-table-column label="物流公司" prop="logisticsCompany" min-width="130" :show-overflow-tooltip="true" />
      <el-table-column label="物流单号" prop="logisticsNo" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="发货状态" prop="deliveryStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="deliveryStatusType(scope.row.deliveryStatus)">{{ deliveryStatusText(scope.row.deliveryStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发货时间" prop="deliveryTime" width="140" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryTime, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签收时间" prop="signTime" width="140" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="发货单详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发货单号">{{ detailInfo.deliveryNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detailInfo.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="统购公告ID">{{ detailInfo.noticeId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="厂商ID">{{ detailInfo.supplierId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ detailInfo.receiverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货电话">{{ detailInfo.receiverPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ detailInfo.receiverAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货状态">{{ deliveryStatusText(detailInfo.deliveryStatus) }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ detailInfo.logisticsCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ detailInfo.logisticsNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ parseTime(detailInfo.deliveryTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="签收时间">{{ parseTime(detailInfo.signTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detailInfo.xfptDeliveryOrderItemList || []" border style="width: 100%; margin-top: 16px;">
        <el-table-column label="商品名称" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="尺码" prop="sizeName" width="120" align="center" />
        <el-table-column label="发货数量" prop="deliveryNum" width="120" align="center" />
        <el-table-column label="签收数量" prop="signNum" width="120" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ itemStatusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getDeliveryStatistics, listDeliveryStatistics } from '@/api/tonggou/tgfh'

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
        deliveryNo: '',
        orderNo: '',
        supplierId: ''
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
        deliveryNo: '',
        orderNo: '',
        supplierId: ''
      }
      this.getList()
    },
    handleView(row) {
      this.detailOpen = true
      this.detailInfo = row
      getDeliveryStatistics(row.id).then(response => {
        this.detailInfo = response.data || row
      })
    },
    deliveryStatusText(status) {
      const textMap = {
        '0': '待发货',
        '1': '已发货',
        '2': '已签收'
      }
      return textMap[status] || '-'
    },
    deliveryStatusType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'success'
      }
      return typeMap[status] || 'info'
    },
    itemStatusText(status) {
      return this.deliveryStatusText(status)
    }
  }
}
</script>
