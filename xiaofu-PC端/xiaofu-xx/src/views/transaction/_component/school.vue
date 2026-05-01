<template>
  <div>
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="区域" prop="parentDeptId">
        <el-select v-model="queryParams.parentDeptId">
          <el-option
            label="全部"
            v-if="$store.state.user.deptId == 100"
            :value="100"
          ></el-option>

          <el-option
            v-for="item of deptList"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- 学校名称 -->
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          placeholder="请输入学校名称"
          v-model="queryParams.schoolName"
        />
      </el-form-item>

      <el-form-item label="公告发布日期" prop="title">
        <el-date-picker
          clearable
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        >
        </el-date-picker>
      </el-form-item>

      <el-form-item label="首次质检占比">
        <el-input-number
          v-model="queryParams.conditionFirstOne"
          :min="0"
          :max="100"
          :precision="0"
          size="small"
          style="width: 150px"
        ></el-input-number>
        <span class="el-range-separator">% — </span>
        <el-input-number
          v-model="queryParams.conditionFirstTwo"
          :min="0"
          :max="100"
          :precision="0"
          size="small"
          style="width: 150px"
        ></el-input-number>
        <span class="el-range-separator">%</span>
      </el-form-item>

      <el-form-item label="二次质检占比">
        <el-input-number
          v-model="queryParams.conditionSecondOne"
          :min="0"
          :max="100"
          :precision="0"
          size="small"
          style="width: 150px"
        ></el-input-number>
        <span class="el-range-separator">% — </span>
        <el-input-number
          v-model="queryParams.conditionSecondTwo"
          :min="0"
          :max="100"
          :precision="0"
          size="small"
          style="width: 150px"
        ></el-input-number>
        <span class="el-range-separator">%</span>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="tableData">
      <el-table-column
        type="index"
        align="center"
        label="序号"
      ></el-table-column>
      <el-table-column
        align="center"
        label="学校名称"
        prop="schoolName"
      ></el-table-column>
      <el-table-column
        align="center"
        label="公告数量"
        prop="ggCount"
      ></el-table-column>
      <el-table-column align="center" label="有质量检测报告及占比">
        <template slot-scope="scope">
          {{ scope.row.hasZjCount }} ({{ scope.row.hasZjRate }})
        </template>
      </el-table-column>
      <el-table-column align="center" label="无质量检测报告量及占比">
        <template slot-scope="scope">
          {{ scope.row.noZjCount }} ({{ scope.row.noZjRate }})
        </template>
      </el-table-column>
      <el-table-column align="center" label="首次质量检测报告量及占比">
        <template slot-scope="scope">
          {{ scope.row.hasFirstZjCount }} ({{ scope.row.hasFirstZjRate }})
        </template>
      </el-table-column>
      <el-table-column align="center" label="二次质量检测报告量及占比">
        <template slot-scope="scope">
          {{ scope.row.hasSecondZjCount }} ({{ scope.row.hasSecondZjRate }})
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
import {
  getDeptListByParentDeptId,
  getSchoolZhiJianReportStatistics,
} from "@/api/jiaoyuju/process";
export default {
  name: "School",
  data() {
    return {
      loading: false,
      dateRange: [],
      deptList: [],
      showSearch: true,
      tableData: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        parentDeptId: null,
        schoolName: "",
        schoolType: "",
        selectType: "",
        bblbValue: "",
        startDate: "",
        endDate: "",
        csName: "",
        conditionFirstOne: 0,
        conditionFirstTwo: 100,
        conditionSecondOne: 0,
        conditionSecondTwo: 100,
      },
    };
  },
  created() {
    this.queryParams.parentDeptId = this.$store.state.user.deptId;
    this.getList();
    this.getDeptList();
  },
  methods: {
    getList() {
      this.loading = true;
      getSchoolZhiJianReportStatistics(
        this.addDateRange({ ...this.queryParams }, this.dateRange)
      ).then((res) => {
        this.tableData = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    getDeptList() {
      getDeptListByParentDeptId({
        parentDeptId: this.$store.state.user.deptId,
      }).then((res) => {
        this.deptList = res.data;
      });
    },
    handleQuery() {
      this.getList();
    },
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.queryParams.parentDeptId = this.$store.state.user.deptId;
      this.queryParams.conditionFirstOne = 0;
      this.queryParams.conditionFirstTwo = 100;
      this.queryParams.conditionSecondOne = 0;
      this.queryParams.conditionSecondTwo = 100;
      this.dateRange = [];
      this.getList();
    },
  },
};
</script>
<style scoped>
.el-range-separator {
  padding: 0 5px;
  line-height: 32px;
  width: auto;
}
</style>
