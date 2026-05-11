<template>
  <div class="app-container delivery-statistics-page">
    <el-card shadow="never" class="search-card">
      <el-form
        v-show="showSearch"
        ref="queryForm"
        :model="queryParams"
        size="small"
        :inline="true"
        label-width="90px"
      >
        <el-form-item label="发货单号" prop="deliveryNo">
          <el-input
            v-model="queryParams.deliveryNo"
            placeholder="请输入发货单号"
            clearable
            style="width: 220px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单号" prop="orderNo">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            style="width: 220px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学校名称" prop="schoolName">
          <el-input
            v-model="queryParams.schoolName"
            placeholder="请输入学校名称"
            clearable
            style="width: 220px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            style="width: 220px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="发货状态" prop="deliveryStatus">
          <el-select v-model="queryParams.deliveryStatus" placeholder="请选择状态" clearable style="width: 220px">
            <el-option
              v-for="item in deliveryStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发货时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="deliveryStatisticsList" border stripe>
      <el-table-column label="发货单号" prop="deliveryNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="订单号" prop="orderNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="学校名称" prop="schoolName" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
      <el-table-column label="发货状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.deliveryStatus)" size="mini">
            {{ getStatusLabel(scope.row.deliveryStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发货数量" prop="deliveryTotalNum" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.deliveryTotalNum == null ? 0 : scope.row.deliveryTotalNum }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签收数量" prop="signTotalNum" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.signTotalNum == null ? 0 : scope.row.signTotalNum }}</span>
        </template>
      </el-table-column>
      <el-table-column label="运费" prop="carriageFee" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.carriageFee == null ? '-' : '￥' + scope.row.carriageFee }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发货时间" width="170" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveryTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签收时间" width="170" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
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
</template>

<script>
import { addDateRange } from '@/utils/ruoyi'
import { listDeliveryStatistics } from '@/api/school/deliveryStatistics'

export default {
  name: 'DeliveryStatistics',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      hasLoaded: false,
      dateRange: [],
      deliveryStatisticsList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deliveryNo: null,
        orderNo: null,
        schoolName: null,
        supplierName: null,
        deliveryStatus: null
      },
      deliveryStatusOptions: [
        { value: '0', label: '待发货' },
        { value: '1', label: '已发货' },
        { value: '2', label: '已签收' }
      ]
    }
  },
  created() {
    this.getList()
  },
  activated() {
    if (this.hasLoaded) {
      this.getList()
    }
  },
  methods: {
    getList() {
      this.loading = true
      const params = addDateRange({ ...this.queryParams }, this.dateRange)
      return listDeliveryStatistics(params).then(response => {
        this.deliveryStatisticsList = response.rows || []
        this.total = response.total || 0
        this.hasLoaded = true
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
      this.dateRange = []
      this.handleQuery()
    },
    getStatusLabel(value) {
      const item = this.deliveryStatusOptions.find(option => option.value === value)
      return item ? item.label : '-'
    },
    getStatusTagType(value) {
      if (value === '2') {
        return 'success'
      }
      if (value === '1') {
        return 'info'
      }
      return 'warning'
    }
  }
}
</script>

<style scoped>
.delivery-statistics-page {
  background: linear-gradient(180deg, #f7fbff 0%, #ffffff 180px);
}

.search-card {
  margin-bottom: 14px;
  border-radius: 12px;
}

.search-card ::v-deep .el-card__body {
  padding-bottom: 4px;
}

.action-row {
  display: flex;
  align-items: center;
}
</style>
