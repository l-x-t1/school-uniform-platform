<template>
  <div class="app-container">
    <!-- 顶部区域：返回、地区筛选、时间筛选、查询按钮等 -->
    <div style="display: flex; align-items: center; margin-bottom: 16px">
      <!-- 返回按钮，点击触发back方法 -->
      <span style="cursor: pointer; color: #409eff" @click="back"
        >订购统计</span
      >
      <span style="margin: 0 8px">&gt;</span>
      <!-- 地区筛选下拉框，选中后触发fetchTypeInfo方法 -->

      <el-select
        v-model="queryParams.deptId"
        placeholder="请选择地区"
        style="width: 140px; margin-right: 16px"
        @change="search"
      >
        <el-option
          v-for="item in areaOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <div style="flex: 1"></div>
      <!-- 统计时间筛选，选择后可用于统计接口参数 -->
      <span style="margin-right: 10px; color: #303133; font-size: 14px"
        >统计时间：</span
      >
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="yyyy-MM-dd"
        value-format="yyyy-MM-dd"
        style="margin-right: 10px"
        @change="search"
      />
      <!-- 查询、本学年、上学年按钮 -->
      <el-button @click="setCurrentYear">本学年</el-button>
      <el-button @click="setLastYear">上学年</el-button>
    </div>
    <!-- 统计类型汇总表格，数据来源getOrderStatisticsTypeInfo接口 -->
    <el-table :data="typeTableData" style="width: 100%; margin-bottom: 60px">
      <el-table-column type="index" label="序号" align="center" />

      <el-table-column prop="district" label="学校类别" align="center" />
      <el-table-column prop="schoolCount" label="学校数量(所)" align="center" />
      <el-table-column
        prop="buySchoolCount"
        label="购买学校数量(所)"
        align="center"
      />
      <el-table-column
        prop="schoolUniformCount"
        label="购买数量(套/件)"
        align="center"
      />
      <el-table-column prop="buyCount" label="成交总金额(元)" align="center" />
    </el-table>
    <!-- 详细筛选条件区域 -->
    <div
      style="margin-bottom: 20px; display: flex; align-items: center; gap: 10px"
    >
      <!-- 学校类别筛选 -->
      <span style="color: #303133; font-size: 14px">学校类别：</span>
      <el-select
        v-model="queryParams.schoolType"
        placeholder="学校类别"
        style="width: 140px"
        @change="fetchData"
      >
        <el-option label="全部" :value="null" />
        <el-option
          v-for="item in dict.type.sc_sc_type"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <!-- 订购状态筛选 -->
      <span style="color: #303133; font-size: 14px">订购状态：</span>
      <el-select
        v-model="queryParams.isBuy"
        placeholder="订购状态"
        style="width: 140px"
        @change="fetchData"
      >
        <el-option
          v-for="item in isBuyOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>

    <!-- 详细数据表格，数据来源getOrderStatisticsByArea接口 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="district" label="学校名称" align="center" />
      <el-table-column label="学校类别" align="center">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sc_sc_type"
            :value="scope.row.schoolType"
          />
        </template>
      </el-table-column>
      <el-table-column prop="ggCount" label="订购公告数量" align="center" />
      <el-table-column
        prop="schoolUniformCount"
        label="订购总数量(套/件)"
        align="center"
      />
      <el-table-column prop="buyCount" label="订购总金额(元)" align="center" />
    </el-table>
  </div>
</template>

<script>
// 引入相关API方法
import {
  getOrderStatisticsByArea,
  getStatisticsSchoolDataList,
  getOrderStatisticsTypeInfo,
  getAreaList,
} from "@/api/jiaoyuju/dingoutongji";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "schoolList",
  dicts: ["sc_sc_type"],
  props: {
    // 父组件可传入地区ID
    areaId: {
      type: Number,
      default: null,
    },
    dateArray: {
      type: Array,
      default: null,
    },
  },
  data() {
    return {
      dateRange: [], // 详细表格的时间范围
      tableData: [], // 详细表格数据
      total: 0, // 详细表格总数
      schoolTypeOptions: [], // 学校类别选项
      isBuyOptions: [
        // 订购状态选项
        { label: "全部", value: null },
        { label: "已完成", value: "1" },
        { label: "未完成", value: "0" },
      ],
      queryParams: {
        schoolType: null, // 学校类别筛选
        isBuy: null, // 订购状态筛选
        deptId: this.areaId, // 地区ID
      },
      areaOptions: [], // 地区下拉选项
      typeDateRange: [], // 统计类型表格的时间范围（如需单独控制可用）
      typeTableData: [], // 统计类型表格数据
    };
  },
  created() {
    if (this.dateArray != null) {
      this.dateRange = this.dateArray;
      this.getAreaOptions(); // 获取地区选项
      this.fetchData();
      this.fetchTypeInfo();
    } else {
      this.setCurrentYear(); // 可选：初始化时间范围
      this.getAreaOptions(); // 获取地区选项
    }
  },
  methods: {
    //搜索按钮
    search() {
      this.queryParams.schoolType = null;
      this.queryParams.isBuy = null;
      this.queryParams = addDateRange(this.queryParams, this.dateRange); // 添加时间范围参数
      this.fetchData();
      this.fetchTypeInfo();
    },
    // 获取详细表格数据
    fetchData() {
      this.queryParams = addDateRange(this.queryParams, this.dateRange); // 添加时间范围参数
      getOrderStatisticsByArea(this.queryParams).then((res) => {
        this.tableData = [...res.data];
      });
    },
    // 设置为本学年时间范围
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
      this.fetchTypeInfo();
    },
    // 设置为上学年时间范围
    setLastYear() {
      const now = new Date();
      let year = now.getFullYear();
      if (now.getMonth() + 1 < 8) {
        year = year - 1;
      }
      const start = `${year - 1}-08-01`;
      const end = `${year}-07-31`;
      this.dateRange = [start, end];
      this.queryParams = addDateRange(this.queryParams, this.dateRange); // 添加时间范围参数
      this.fetchData();
      this.fetchTypeInfo();
    },
    // 行点击事件（可扩展）
    handleRowClick(row, column, event, rowIndex) {
      if (rowIndex === 0) return;
      // 这里只处理第二行及以后
      console.log("点击的行对象：", row);
    },
    // 获取地区下拉选项
    getAreaOptions() {
      getAreaList().then((res) => {
        this.areaOptions = (res.data || []).map((item) => ({
          label: item.deptName,
          value: item.deptId,
        }));
      });
    },
    // 获取顶部统计类型汇总表格数据
    fetchTypeInfo() {
      getOrderStatisticsTypeInfo(this.queryParams).then((res) => {
        this.typeTableData = [...res.data];
      });
    },
    // 返回上一页
    back() {
      this.$emit("back");
    },
  },
};
</script>

<style scoped></style>
