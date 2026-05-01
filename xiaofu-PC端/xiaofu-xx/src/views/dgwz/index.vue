<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="评价状态">
        <el-select v-model="queryParams.businessType" placeholder="请选择" style="width: 220px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="校服采购" value="1"></el-option>
          <el-option label="办公用品" value="2"></el-option>
          <el-option label="其他" value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单号">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>

    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData">
      <!-- <el-table-column label="厂商名称" prop="manufacturerName" min-width="180"></el-table-column>
      <el-table-column label="订单笔数" prop="orderCount" align="center" width="120"></el-table-column>
      <el-table-column label="订购数量" prop="orderQuantity" align="center" width="120"></el-table-column> -->
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'DgmxIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        beginTime: '2024-04-17 00:00:00',
        endTime: '2025-04-17 23:59:59',
        manufacturerId: '',
        businessType: ''
      },
      // 厂商选项
      manufacturerOptions: [
        { value: '1', label: '某服装厂' },
        { value: '2', label: '某制衣厂' },
        { value: '3', label: '某校服厂' }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 模拟接口调用
      setTimeout(() => {
        // 生成模拟数据
        if (this.queryParams.manufacturerId || this.queryParams.businessType) {
          this.tableData = [
            {
              manufacturerName: '某服装厂',
              orderCount: 10,
              orderQuantity: 200
            },
            {
              manufacturerName: '某制衣厂',
              orderCount: 15,
              orderQuantity: 350
            },
            {
              manufacturerName: '某校服厂',
              orderCount: 8,
              orderQuantity: 160
            }
          ];
          this.total = this.tableData.length;
        } else {
          this.tableData = [];
          this.total = 0;
        }
        this.loading = false;
      }, 300);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有订单明细数据?').then(() => {
        this.$modal.msgSuccess("导出成功");
      }).catch(() => { });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-form {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.empty-block {
  text-align: center;
  padding: 30px 0;
  color: #909399;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
</style>
