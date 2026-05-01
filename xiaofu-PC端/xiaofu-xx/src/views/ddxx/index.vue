<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item>
        <el-select v-model="queryParams.queryType" placeholder="请选择查询类型" style="width: 150px;">
          <el-option label="订单号" value="order"></el-option>
          <el-option label="流水号" value="serial"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.queryValue" placeholder="订单号不能为空" style="width: 240px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" style="width: 100%; margin-top: 15px;">
      <!-- 表格列将根据查询结果填充 -->
    </el-table>



    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'DdxxIndex',
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
        queryType: 'order',
        queryValue: ''
      }
    };
  },
  computed: {
    // 判断是否禁用搜索按钮
    isSearchDisabled() {
      return !this.queryParams.queryValue;
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      if (!this.queryParams.queryValue) {
        return;
      }
      this.loading = true;
      // 模拟接口调用
      setTimeout(() => {
        this.tableData = [];
        this.total = 0;
        this.loading = false;
      }, 300);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if (!this.queryParams.queryValue) {
        this.$message.warning('查询条件不能为空');
        return;
      }
      this.queryParams.pageNum = 1;
      this.getList();
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.empty-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}
</style>
