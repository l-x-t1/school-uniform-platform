<template>
  <div class="app-container">
    <el-alert
      title="此页面可查询与自己参与的浏览商品已关联采购，您也可查询浏览商品没关联采购，您可查看个商品。"
      type="warning"
      :closable="false"
      show-icon
      class="alert-warning mb15"
    />

    <el-form :inline="true" :model="queryParams">
      <el-form-item label="导出快查：">
        <el-select v-model="queryParams.exportType" placeholder="请选择" style="width: 180px;">
          <el-option label="待导出" value="1"></el-option>
          <el-option label="已导出" value="2"></el-option>
          <el-option label="全部" value=""></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="厂商名称：">
        <el-select v-model="queryParams.manufacturerId" placeholder="请选择" style="width: 180px;">
          <el-option label="全部" value=""></el-option>
          <el-option v-for="item in manufacturerOptions" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="公告名称" prop="noticeName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="付款截止日期" prop="paymentDeadline" width="150" align="center"></el-table-column>
      <el-table-column label="预计到货日期" prop="expectedArrivalDate" width="150" align="center"></el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : scope.row.status === '2' ? 'warning' : 'info'">
            {{ scope.row.statusText }}
          </el-tag>
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
import { listDeliveryOrderStatistics, listDeliveryOrderStatisticsSuppliers } from '@/api/school/deliveryOrder';

export default {
  name: 'TgfhIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        exportType: '',
        manufacturerId: ''
      },
      manufacturerOptions: []
    };
  },
  created() {
    this.getSupplierOptions();
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listDeliveryOrderStatistics(this.queryParams).then(response => {
        this.tableData = response.rows || [];
        this.total = response.total || 0;
      }).finally(() => {
        this.loading = false;
      });
    },
    getSupplierOptions() {
      listDeliveryOrderStatisticsSuppliers().then(response => {
        this.manufacturerOptions = response.data || [];
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    }
  }
};
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
