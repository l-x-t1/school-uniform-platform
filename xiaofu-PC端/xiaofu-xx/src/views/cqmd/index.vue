<template>
  <div class="app-container">
    <!-- -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="文件名：">
        <el-input v-model="queryParams.name" placeholder="请输入文件名" clearable style="width: 240px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-button type="text">下载模板</el-button>
    <el-button type="primary" size="small">导入投票人</el-button>
    <el-table v-loading="loading" :data="tableData" fit highlight-current-row style="width: 100%;margin-top: 10px;">
      <el-table-column label="投票人文件名" prop="name"></el-table-column>
      <el-table-column label="投票人数" prop="name"></el-table-column>
      <el-table-column label="包含角色" prop="name"></el-table-column>
      <el-table-column label="更新时间" prop="name"></el-table-column>
      <el-table-column label="操作" prop="name"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'RuoyiXxIndex',

  data() {
    return {
      queryParams: {
        name: '',
      },
      tableData: [],
      loading: false,
    };
  },

  mounted() {

  },

  methods: {

  },
};
</script>