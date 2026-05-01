<template>
  <div class="app-container">
    <!-- -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="入账日期：">
        <el-date-picker v-model="queryParams.dateRange" type="datetimerange" value-format="yyyy-MM-dd"
          style="width: 240px"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary">查询</el-button>
      </el-form-item>
    </el-form>
    <el-button size="small" type="primary">列表导出</el-button>
    <el-table :data="tableData" style="width: 100%;margin-top: 10px;">
      <el-table-column prop="" label="入账日期"></el-table-column>
      <el-table-column prop="" label="交易日期"></el-table-column>
      <el-table-column prop="" label="入账金额"></el-table-column>
      <el-table-column prop="" label="操作"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'RuoyiXxIndex',

  data() {
    return {
      queryParams: {
        dateRange: ''
      },
      tableData: []
    };
  },

  mounted() {

  },

  methods: {

  },
};
</script>