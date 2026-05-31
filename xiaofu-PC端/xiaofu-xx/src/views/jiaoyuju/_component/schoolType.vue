<template>
  <div>
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="区域">
        <el-select v-model="queryParams.parentDeptId">
          <el-option
            v-for="item of deptList"
            :key="item.deptId"
            :label="item.deptName"
            :value="item.deptId"
          ></el-option>
        </el-select>
      </el-form-item>
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
      </el-form-item>
    </el-form>

    <el-table
      height="700px"
      v-loading="loading"
      :data="dataTypeList"
      style="width: 100%"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column prop="name" label="学校类别" />
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
    </el-table>
  </div>
</template>

<script>
import {
  getStatisticsSchoolDataList,
  getDeptListByParentDeptId,
} from "@/api/jiaoyuju/process";

export default {
  dicts: ["sc_sc_type"],
  name: "schoolType",
  props: {
    currentRow: {},
  },
  data() {
    return {
      loading: false,
      schoolYear: "本学年",
      dataTypeList: [],
      queryParams: {
        parentDeptId: null,
      },
      dateRange: [],
      deptList: [],
    };
  },
  created() {
    this.setDateRangeBySchoolYear("本学年");

    this.queryParams.parentDeptId = this.currentRow.deptId;
    this.getDeptList();
    this.getList();
  },
  methods: {
    //获取部门
    getDeptList() {
      getDeptListByParentDeptId({ parentDeptId: this.currentRow.deptId }).then(
        (res) => {
          this.deptList = res.data;
        }
      );
    },
    handleQuery() {
      this.getList();
    },
    // 重置查询条件
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.setDateRangeBySchoolYear(this.schoolYear);
      this.getList();
    },
    getList() {
      this.loading = true;
      getStatisticsSchoolDataList(
        this.addDateRange({ ...this.queryParams }, this.dateRange)
      ).then((res) => {
        this.dataTypeList = res.data;
        this.loading = false;
      });
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
  },
  watch: {
    // 监听学年变化，自动更新日期范围
    schoolYear(newVal) {
      this.setDateRangeBySchoolYear(newVal);
    },
  },
};
</script>

<style scoped></style>
