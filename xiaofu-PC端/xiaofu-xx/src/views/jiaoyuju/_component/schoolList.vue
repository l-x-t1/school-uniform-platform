<template>
  <div>
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      :inline="true"
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
      <!-- 学校名称 -->
      <el-form-item label="学校名称" prop="schoolName">
        <el-input
          placeholder="请输入学校名称"
          v-model="queryParams.schoolName"
        />
      </el-form-item>
      <!-- 学校类别 -->
      <el-form-item label="学校类别" prop="schoolType">
        <el-select v-model="queryParams.schoolType">
          <el-option
            v-for="dict of dict.type.sc_sc_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="流程状态" prop="selectType">
        <el-select
          @change="handleChangeSelect"
          v-model="queryParams.selectType"
        >
          <el-option
            v-for="dict of dict.type.dic_bblb"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="状态" prop="bblbValue">
        <el-select
          v-if="queryParams.selectType != '2'"
          v-model="queryParams.bblbValue"
        >
          <el-option label="全部" value="" />

          <el-option label="已报备" value="已报备" />
          <el-option label="未报备" value="未报备" />
        </el-select>
        <el-select v-else v-model="queryParams.bblbValue">
          <el-option label="全部" value="" />

          <el-option label="已发起" value="已发起" />
          <el-option label="未发起" value="未发起" />
        </el-select>
      </el-form-item>
      <el-form-item label="报名日期" prop="title">
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

    <el-table v-loading="loading" :data="statisticsDataList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column align="center" prop="schoolName" label="学校名称">
        <template slot-scope="scope">
          <span class="link-text" @click="handleLink(scope.row)">
            {{ scope.row.schoolName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="bzhyFlg"
        label="班子会议纪要报备与公开"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.bzhyFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="jzgmyjFlg"
        label="发布采购公告"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.jzgmyjFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="zzmdFlg"
        label="备选公告展示"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.zzmdFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="xyzzxgmsFlg"
        label="家长投票"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.xyzzxgmsFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="xgjggsFlg"
        label="投票结果公示"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.xgjggsFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column 
        align="center" 
        prop="cghtFlg" 
        label="签订合同" 
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.cghtFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="cgjgggFlg"
        label="采购结果公告"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.cgjgggFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="cpzljybgFlg"
        label="产品质量检验报告报备与公开"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.cpzljybgFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="jzmydzjbgFlg"
        label="选用组织家长满意度总结报告报备与公开"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.jzmydzjbgFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="zjbbFlg"
        label="采购信息报备"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.zjbbFlg == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="cggzzjFlag"
        label="学校校服采购工作总结"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.cggzzjFlag == '已报备'" type="success">已报备</el-tag>
          <el-tag v-else type="danger">未报备</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="hanldeProcess(scope.row)"
            >查看流程</el-button
          >
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
  getSchoolBaoBeiSituationStatistics,
  getDeptListByParentDeptId,
} from "@/api/jiaoyuju/process";
export default {
  dicts: ["dic_bblb", "sc_sc_type"],
  name: "schoolList",
  props: {
    currentRow: {
      type: Object,
      default: () => {},
    },
    dateRange: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      loading: false,
      total: 0,
      statisticsDataList: [],
      showSearch: true,
      schoolYear: "本学年", 
      deptList: [],
      queryParams: {
        schoolName: "",
        parentDeptId: "",
        schoolType: "",
        selectType: "12",
        bblbValue: "",
        startDate: "",
        endDate: "",
        pageNum: "",
        pageSize: "",
      },
    };
  },
  created() {
    this.queryParams.parentDeptId = this.currentRow.deptId;
    this.getList();
    this.getDeptList();
  },
  methods: {
    //查看流程
    hanldeProcess(row) {
      this.$router.push({
        path: "/cg",
        query: {
          deptId: row.deptId,
        },
      });
    },
    handleChangeSelect() {
      this.queryParams.bblbValue = null;
    },
    //获取部门
    getDeptList() {
      getDeptListByParentDeptId({
        parentDeptId: this.currentRow.deptId,
      }).then((res) => {
        this.deptList = res.data;
      });
    },
    // 查询方法
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
      getSchoolBaoBeiSituationStatistics(
        this.addDateRange(this.queryParams, this.dateRange)
      ).then((res) => {
        this.statisticsDataList = res.rows;
        this.total = res.total;
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
    handleExport() {
      this.download(
        "school/team/meeting/exportSchoolBaoBeiSituationStatistics",
        this.addDateRange(this.queryParams, this.dateRange),
        `报备情况.xlsx`
      );
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
