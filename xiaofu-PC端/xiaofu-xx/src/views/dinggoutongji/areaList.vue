<template>
  <div class="app-container">
    <div style="margin-bottom: 20px">
      <el-form :inline="true">
        <el-form-item label="订购日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="setCurrentYear">本学年</el-button>
          <el-button @click="setLastYear">上学年</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      @row-click="handleRowClick"
    >
      <el-table-column width="100" type="index" label="序号" />

      <el-table-column prop="district" label="区县名称">
        <template slot-scope="scope">
          <span class="district">{{ scope.row.district }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="buySchoolCount" label="购买学校数量(所)" />
      <el-table-column prop="schoolUniformCount" label="校服购买数量(套/件)" />
      <el-table-column prop="buyCount" label="购买金额(元)" />
    </el-table>
  </div>
</template>

<script>
import { getOrderStatisticsOverview } from "@/api/jiaoyuju/dingoutongji";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "areaList",
  data() {
    return {
      dateRange: [],
      tableData: [],
      queryParams: {},
    };
  },
  created() {
    this.setCurrentYear();
  },
  methods: {
    fetchData() {
      let params = Object.assign({}, this.queryParams);
      params = addDateRange(params, this.dateRange);
      this.$emit("changeTime", this.dateRange);
      getOrderStatisticsOverview(params).then((res) => {
        this.tableData = res.data;
      });
    },
    setCurrentYear() {
      const now = new Date();
      let year = now.getFullYear();
      if (now.getMonth() + 1 < 8) {
        year = year - 1;
      }
      const start = `${year}-08-01`;
      const end = `${year + 1}-07-31`;
      this.dateRange = [start, end];
      this.fetchData();
    },
    setLastYear() {
      const now = new Date();
      let year = now.getFullYear();
      if (now.getMonth() + 1 < 8) {
        year = year - 1;
      }
      const start = `${year - 1}-08-01`;
      const end = `${year}-07-31`;
      this.dateRange = [start, end];
      this.fetchData();
    },
    // 行点击事件，第一行不响应
    handleRowClick(row) {
      console.log(row);
      if (row.deptId === null) return;
      // 这里只处理第二行及以后
      this.$emit("area-click", row);
      // 你可以在这里做你想要的操作
    },
  },
};
</script>

<style scoped>
.district {
  cursor: pointer;
}
</style>
