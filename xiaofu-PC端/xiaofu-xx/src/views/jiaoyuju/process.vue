<template>
  <div class="app-container">
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="统计时间" prop="title">
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
      <el-form-item prop="schoolId">
        <el-radio-group v-model="schoolYear">
          <el-radio-button label="本学年">本学年</el-radio-button>
          <el-radio-button label="上学年">上学年</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-download" @click="handleExport"
          >导出数据</el-button
        >
      </el-form-item>
    </el-form>

    <el-table :data="statisticsDataList" >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column align="center" prop="name" label="区县名称">
        <template slot-scope="scope">
          <span
            v-if="scope.row.name != '总数'"
            class="link-text"
            @click="handleLink(scope.row)"
          >
            {{ scope.row.name }}
          </span>
          <span v-else>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column 
        align="center"
        prop="bzhyTotal"
        label="班子会议纪要报备与公开学校数"
      />
      <el-table-column 
        align="center"
        prop="jzgmyjTotal"
        label="发布采购公告学校数"
      />
      <el-table-column 
        align="center"
        prop="zzmdTotal"
        label="备选公告展示学校数"
      />
      <el-table-column 
        align="center"
        prop="zzxgmsTotal"
        label="家长投票学校数"
      />
      <el-table-column 
        align="center"
        prop="xgjggsTotal"
        label="投票结果公示学校数"
      />
      <el-table-column 
        align="center"
        prop="cghtTotal"
        label="签订合同学校数"
      />
      <el-table-column 
        align="center"
        prop="cgjgggTotal"
        label="采购结果公告学校数"
      />
      <el-table-column 
        align="center"
        prop="cpzljcTotal"
        label="产品质量检验报告报备与公开学校数"
      />
      <el-table-column 
        align="center"
        prop="jzmydzjbgTotal"
        label="选用组织家长满意度总结报告报备与公开学校数"
      />
      <el-table-column 
        align="center"
        prop="zjbbTotal"
        label="采购信息报备学校数"
      />
      <el-table-column 
        align="center"
        prop="cggzzjTotal"
        label="学校校服采购工作总结学校数"
      />
      <el-table-column width="100" align="center" label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.deptId"
            type="text"
            @click="handleLink(scope.row)"
            >查看</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加弹窗 -->
    <el-dialog
      fullscreen
      title="学校详情"
      :visible.sync="dialogVisible"
      width="80%"
      append-to-body
    >
      <!-- <el-tabs v-if="dialogVisible" v-model="activeTab">
        <el-tab-pane label="学校类别" name="schoolType">
          <school-type :currentRow="currentRow"></school-type>
        </el-tab-pane>
        <el-tab-pane label="学校列表" name="schoolList"> -->
          <school-list v-if="dialogVisible"
            :currentRow="currentRow"
            :date-range="dateRange"
          ></school-list>
        <!-- </el-tab-pane>
      </el-tabs> -->
    </el-dialog>
  </div>
</template>

<script>
import schoolList from "./_component/schoolList";
import schoolType from "./_component/schoolType";
import { getStatisticsDataList } from "@/api/jiaoyuju/process";
export default {
  components: {
    schoolList,
    schoolType,
  },
  data() {
    return {
      statisticsDataList: [],
      showSearch: true,
      schoolYear: "本学年",
      dateRange: [],
      queryParams: {
        parentDeptId: null,
      },
      dialogVisible: false,
      activeTab: "schoolType",
      currentRow: null,
    };
  },
  created() {
    // 默认设置为本学年
    this.setDateRangeBySchoolYear("本学年");
    this.getList();
  },
  methods: {
    //获取数据
    getList() {
      getStatisticsDataList(
        this.addDateRange(
          { ...this.queryParams, parentDeptId: this.$store.state.user.deptId },
          this.dateRange
        )
      ).then((res) => {
        this.statisticsDataList = res.data;
      });
    },
    handleExport() {
      this.download(
        "school/team/meeting/exportDistrictCountryStatistics",
        this.addDateRange(
          { ...this.queryParams, parentDeptId: this.$store.state.user.deptId },
          this.dateRange
        ),
        `校服选用采购流程规范管理.xlsx`
      );
    },
    // 查询方法
    handleQuery() {
      this.getList();
    },
    // 重置查询条件
    resetQuery() {
      this.setDateRangeBySchoolYear(this.schoolYear);
      this.getList();
    },
    // 根据学年设置日期范围
    setDateRangeBySchoolYear(year) {
      const currentDate = new Date();
      const currentYear = currentDate.getFullYear();
      const currentMonth = currentDate.getMonth() + 1;

      // 学年开始月份通常为8月，如果当前月份小于8月，则认为是上一年的8月开始
      let startYear, endYear;

      if (year === "本学年") {
        if (currentMonth < 8) {
          // 如果当前月份小于8月，则本学年是上一年8月到今年7月
          startYear = currentYear - 1;
          endYear = currentYear;
        } else {
          // 如果当前月份大于等于8月，则本学年是今年8月到明年7月
          startYear = currentYear;
          endYear = currentYear + 1;
        }
      } else if (year === "上学年") {
        if (currentMonth < 8) {
          // 如果当前月份小于8月，则上学年是上上一年8月到上一年7月
          startYear = currentYear - 2;
          endYear = currentYear - 1;
        } else {
          // 如果当前月份大于等于8月，则上学年是上一年8月到今年7月
          startYear = currentYear - 1;
          endYear = currentYear;
        }
      }

      // 设置日期范围，格式为 yyyy-MM-dd
      this.dateRange = [`${startYear}-08-01`, `${endYear}-07-31`];
    },
    // 点击区县名称链接处理
    handleLink(row) {
      this.currentRow = row;
      this.dialogVisible = true;
      this.activeTab = "schoolType";
    },
    // 添加日期范围的方法（如果不存在）
    addDateRange(params, dateRange) {
      if (dateRange != null && dateRange.length > 0) {
        const obj = { ...params };
        obj.beginTime = dateRange[0];
        obj.endTime = dateRange[1];
        return obj;
      }
      return params;
    },
  },
  watch: {
    // 监听学年变化，自动更新日期范围
    schoolYear(newVal) {
      this.setDateRangeBySchoolYear(newVal);
    },
  },
};
</script>

<style scoped>
.link-text:hover {
  display: inline-block;
  color: skyblue;
  text-decoration: underline;
  cursor: pointer;
}
</style>
