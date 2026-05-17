<template>
  <div v-loading="loading" class="app-container usage-dashboard">
    <div class="page-header">
      <div>
        <div class="page-title">使用情况</div>
        <div class="page-desc">
          整合平台使用情况、学校使用情况、教育局使用情况和价格趋势分析，支持按部门 ID 查询。
        </div>
      </div>
      <el-form :model="queryParams" size="small" inline class="filter-form" @submit.native.prevent>
        <el-form-item label="部门ID">
          <el-input
            v-model="queryParams.deptId"
            clearable
            placeholder="留空自动使用当前部门"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="section-block">
      <div class="section-head">
        <div>
          <div class="section-title">平台使用情况</div>
          <div class="section-tip">区县平台、学校平台、学生平台整体接入与本年数据表现。</div>
        </div>
      </div>

      <el-row :gutter="16" class="metric-grid">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card blue">
            <div class="metric-label">本年订购人数</div>
            <div class="metric-value">{{ peopleStats.thisOrderYearCount || 0 }}</div>
            <div class="metric-sub">去年 {{ peopleStats.lastOrderYearCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card green">
            <div class="metric-label">本年订购金额</div>
            <div class="metric-value">￥{{ formatMoney(peopleStats.thisMoneyYearCount) }}</div>
            <div class="metric-sub">去年 ￥{{ formatMoney(peopleStats.lastMoneyYearCount) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card orange">
            <div class="metric-label">区县平台总量</div>
            <div class="metric-value">{{ platformUsage.districtPlatformTotal || 0 }}</div>
            <div class="metric-sub">平台覆盖概览</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="metric-card red">
            <div class="metric-label">学校 / 学生平台总量</div>
            <div class="metric-value">{{ platformUsage.schoolPlatformTotal || 0 }}</div>
            <div class="metric-sub">学生平台 {{ platformUsage.studentPlatformTotal || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-grid">
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">今年订购人数统计</div>
            <div ref="orderCountChart" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">今年金额统计</div>
            <div ref="orderAmountChart" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">平台使用情况</div>
            <div ref="platformUsageChart" class="chart-box"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section-block">
      <div class="section-head">
        <div>
          <div class="section-title">学校使用情况</div>
          <div class="section-tip">学校端采购规模、近五年趋势和校服品类统计。</div>
        </div>
      </div>

      <el-row :gutter="16" class="metric-grid">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="mini-card">
            <div class="mini-title">总订购金额</div>
            <div class="mini-value">￥{{ formatMoney(schoolSummary.moneyTotal) }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="mini-card">
            <div class="mini-title">总订购笔数</div>
            <div class="mini-value">{{ schoolSummary.countTotal || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="mini-card">
            <div class="mini-title">总订购人数</div>
            <div class="mini-value">{{ schoolSummary.studentTotal || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="mini-card">
            <div class="mini-title">班子 / 团队数</div>
            <div class="mini-value">{{ schoolSummary.teamCount || 0 }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-grid">
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">学校近5年订购人数</div>
            <div ref="fiveYearCountChart" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">学校近5年金额统计</div>
            <div ref="fiveYearMoneyChart" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">校服品类统计</div>
            <div ref="uniformTypeChart" class="chart-box"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section-block">
      <div class="section-head">
        <div>
          <div class="section-title">教育局使用情况</div>
          <div class="section-tip">各县区教育局视角下的学生、学校、供应商和金额统计。</div>
        </div>
      </div>

      <el-row :gutter="16" class="metric-grid">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="neutral-card">
            <div class="neutral-title">学生总数</div>
            <div class="neutral-value">{{ jyjSummary.studentTotal || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="neutral-card">
            <div class="neutral-title">学校总数</div>
            <div class="neutral-value">{{ jyjSummary.schoolTotal || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="neutral-card">
            <div class="neutral-title">供应商总数</div>
            <div class="neutral-value">{{ jyjSummary.supplierTotal || 0 }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="neutral-card">
            <div class="neutral-title">采购总金额</div>
            <div class="neutral-value">￥{{ formatMoney(jyjSummary.moneyTotal) }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-grid">
        <el-col :xs="24">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">教育局使用情况概览</div>
            <div ref="jyjSummaryChart" class="chart-box"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section-block">
      <div class="section-head">
        <div>
          <div class="section-title">价格趋势分析</div>
          <div class="section-tip">展示价格变化趋势，辅助决策分析。</div>
        </div>
      </div>

      <el-row :gutter="16" class="chart-grid">
        <el-col :xs="24">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">价格趋势分析</div>
            <div ref="priceTrendChart" class="chart-box large"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons");
import {
  getJyjTopList,
  getOrderAmount,
  getOrderCount,
  getPersonMoney,
  getPlatformUsage,
  getPriceTrendData,
  getSchoolOrderAmount,
  getSchoolOrderCount,
  getSchoolTopList,
  getSchoolUniformType,
} from "@/api/index";

export default {
  name: "UsageDashboard",
  data() {
    return {
      loading: false,
      queryParams: {
        deptId: "",
      },
      peopleStats: {},
      platformUsage: {},
      schoolSummary: {},
      jyjSummary: {},
      currentYearCountList: [],
      currentYearMoneyList: [],
      schoolFiveYearCountList: [],
      schoolFiveYearMoneyList: [],
      uniformTypeList: [],
      priceTrendList: [],
      orderCountChart: null,
      orderAmountChart: null,
      fiveYearCountChart: null,
      fiveYearMoneyChart: null,
      uniformTypeChart: null,
      platformUsageChart: null,
      jyjSummaryChart: null,
      priceTrendChart: null,
      resizeHandler: null,
    };
  },
  mounted() {
    this.resizeHandler = () => {
      [
        this.orderCountChart,
        this.orderAmountChart,
        this.fiveYearCountChart,
        this.fiveYearMoneyChart,
        this.uniformTypeChart,
        this.platformUsageChart,
        this.jyjSummaryChart,
        this.priceTrendChart,
      ].forEach((chart) => chart && chart.resize());
    };
    window.addEventListener("resize", this.resizeHandler);
    this.loadData();
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeHandler);
    this.disposeCharts();
  },
  methods: {
    buildQuery() {
      if (this.queryParams.deptId === "" || this.queryParams.deptId === null || this.queryParams.deptId === undefined) {
        return {};
      }
      const deptId = Number(this.queryParams.deptId);
      return Number.isNaN(deptId) ? {} : { deptId };
    },
    extractData(response, fallback) {
      if (!response) return fallback;
      const payload = response.data !== undefined ? response.data : response.rows;
      return payload !== undefined && payload !== null ? payload : fallback;
    },
    formatMoney(value) {
      const num = Number(value || 0);
      return Number.isFinite(num)
        ? num.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
        : "0.00";
    },
    toPoints(list) {
      return (Array.isArray(list) ? list : []).map((item) => ({
        name: item.key || item.name || item.label || "未知",
        value: Number(item.value ?? item.count ?? item.amount ?? 0),
      }));
    },
    handleQuery() {
      this.loadData();
    },
    resetQuery() {
      this.queryParams.deptId = "";
      this.loadData();
    },
    async loadData() {
      this.loading = true;
      const query = this.buildQuery();
      try {
        const tasks = await Promise.allSettled([
          getPersonMoney(),
          getOrderCount(),
          getOrderAmount(),
          getSchoolOrderCount(query),
          getSchoolOrderAmount(query),
          getSchoolUniformType(),
          getPlatformUsage(query),
          getPriceTrendData(query),
          getSchoolTopList(query),
          getJyjTopList(query),
        ]);

        const [
          peopleRes,
          countRes,
          moneyRes,
          fiveYearCountRes,
          fiveYearMoneyRes,
          uniformTypeRes,
          usageRes,
          priceTrendRes,
          schoolSummaryRes,
          jyjSummaryRes,
        ] = tasks;

        this.peopleStats = peopleRes.status === "fulfilled" ? this.extractData(peopleRes.value, {}) : {};
        this.currentYearCountList = countRes.status === "fulfilled" ? this.extractData(countRes.value, []) : [];
        this.currentYearMoneyList = moneyRes.status === "fulfilled" ? this.extractData(moneyRes.value, []) : [];
        this.schoolFiveYearCountList =
          fiveYearCountRes.status === "fulfilled" ? this.extractData(fiveYearCountRes.value, []) : [];
        this.schoolFiveYearMoneyList =
          fiveYearMoneyRes.status === "fulfilled" ? this.extractData(fiveYearMoneyRes.value, []) : [];
        this.uniformTypeList = uniformTypeRes.status === "fulfilled" ? this.extractData(uniformTypeRes.value, []) : [];
        this.platformUsage = usageRes.status === "fulfilled" ? this.extractData(usageRes.value, {}) : {};
        this.priceTrendList = priceTrendRes.status === "fulfilled" ? this.extractData(priceTrendRes.value, []) : [];
        this.schoolSummary = schoolSummaryRes.status === "fulfilled" ? this.extractData(schoolSummaryRes.value, {}) : {};
        this.jyjSummary = jyjSummaryRes.status === "fulfilled" ? this.extractData(jyjSummaryRes.value, {}) : {};

        const failedCount = tasks.filter((item) => item.status === "rejected").length;
        this.$nextTick(() => {
          this.renderCharts();
        });
        if (failedCount > 0) {
          this.$message.warning(`有 ${failedCount} 个统计接口加载失败，页面已展示成功返回的数据`);
        }
      } finally {
        this.loading = false;
      }
    },
    disposeCharts() {
      [
        this.orderCountChart,
        this.orderAmountChart,
        this.fiveYearCountChart,
        this.fiveYearMoneyChart,
        this.uniformTypeChart,
        this.platformUsageChart,
        this.jyjSummaryChart,
        this.priceTrendChart,
      ].forEach((chart) => chart && chart.dispose());

      this.orderCountChart = null;
      this.orderAmountChart = null;
      this.fiveYearCountChart = null;
      this.fiveYearMoneyChart = null;
      this.uniformTypeChart = null;
      this.platformUsageChart = null;
      this.jyjSummaryChart = null;
      this.priceTrendChart = null;
    },
    renderCharts() {
      this.disposeCharts();

      this.renderBarChart(
        "orderCountChart",
        "今年订购人数统计",
        this.currentYearCountList,
        { color: "#3b82f6", yName: "人数", isMoney: false },
        (chart) => {
          this.orderCountChart = chart;
        }
      );

      this.renderBarChart(
        "orderAmountChart",
        "今年金额统计",
        this.currentYearMoneyList,
        { color: "#22c55e", yName: "金额(元)", isMoney: true },
        (chart) => {
          this.orderAmountChart = chart;
        }
      );

      this.renderLineChart(
        "fiveYearCountChart",
        "学校近5年订购人数",
        this.schoolFiveYearCountList,
        "人数",
        "#6366f1",
        (chart) => {
          this.fiveYearCountChart = chart;
        }
      );

      this.renderLineChart(
        "fiveYearMoneyChart",
        "学校近5年金额统计",
        this.schoolFiveYearMoneyList,
        "金额(元)",
        "#f59e0b",
        (chart) => {
          this.fiveYearMoneyChart = chart;
        },
        true
      );

      this.renderPieChart("uniformTypeChart", "校服品类统计", this.uniformTypeList, (chart) => {
        this.uniformTypeChart = chart;
      });

      this.renderPlatformUsageChart((chart) => {
        this.platformUsageChart = chart;
      });

      this.renderJyjSummaryChart((chart) => {
        this.jyjSummaryChart = chart;
      });

      this.renderLineChart(
        "priceTrendChart",
        "价格趋势分析",
        this.priceTrendList,
        "金额(元)",
        "#ef4444",
        (chart) => {
          this.priceTrendChart = chart;
        },
        true
      );
    },
    renderBarChart(refName, title, list, style, setter) {
      const dom = this.$refs[refName];
      if (!dom) return;
      const chart = echarts.init(dom, "macarons");
      const points = this.toPoints(list);

      chart.setOption({
        title: {
          text: title,
          left: "center",
          top: 8,
          textStyle: { fontSize: 14, color: "#334155" },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "shadow" },
          backgroundColor: "rgba(37, 99, 235, 0.92)",
          borderColor: "rgba(37, 99, 235, 0.8)",
          textStyle: { color: "#fff" },
          formatter: (params) => {
            const item = params && params[0];
            if (!item) return "";
            const value = style.isMoney ? `￥${Number(item.value || 0).toLocaleString()}` : item.value;
            return `${item.name}<br/>${item.seriesName}: ${value}`;
          },
        },
        grid: { left: 20, right: 20, top: 60, bottom: 30, containLabel: true },
        xAxis: {
          type: "category",
          data: points.map((item) => item.name),
          axisLabel: { color: "#64748b", interval: 0, rotate: points.length > 6 ? 25 : 0 },
          axisLine: { lineStyle: { color: "#cbd5e1" } },
        },
        yAxis: {
          type: "value",
          name: style.yName,
          axisLabel: {
            color: "#64748b",
            formatter: (value) => (style.isMoney ? `￥${Math.round(value)}` : `${Math.round(value)}`),
          },
          splitLine: { lineStyle: { color: "#e2e8f0", type: "dashed" } },
        },
        series: [
          {
            name: title,
            type: "bar",
            barWidth: "45%",
            data: points.map((item) => item.value),
            itemStyle: {
              borderRadius: [6, 6, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: style.color },
                { offset: 1, color: "#bfdbfe" },
              ]),
            },
            label: { show: true, position: "top", color: "#334155" },
          },
        ],
      });

      setter(chart);
    },
    renderLineChart(refName, title, list, yName, color, setter, isMoney = false) {
      const dom = this.$refs[refName];
      if (!dom) return;
      const chart = echarts.init(dom, "macarons");
      const points = this.toPoints(list);

      chart.setOption({
        title: {
          text: title,
          left: "center",
          top: 8,
          textStyle: { fontSize: 14, color: "#334155" },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "line" },
          backgroundColor: "rgba(2, 132, 199, 0.92)",
          borderColor: "rgba(2, 132, 199, 0.8)",
          textStyle: { color: "#fff" },
          formatter: (params) => {
            const item = params && params[0];
            if (!item) return "";
            const value = isMoney ? `￥${Number(item.value || 0).toLocaleString()}` : `${item.value}`;
            return `${item.name}<br/>${item.seriesName}: ${value}`;
          },
        },
        grid: { left: 20, right: 20, top: 60, bottom: 30, containLabel: true },
        xAxis: {
          type: "category",
          data: points.map((item) => item.name),
          axisLabel: { color: "#64748b", interval: 0, rotate: points.length > 6 ? 25 : 0 },
          axisLine: { lineStyle: { color: "#cbd5e1" } },
        },
        yAxis: {
          type: "value",
          name: yName,
          axisLabel: {
            color: "#64748b",
            formatter: (value) => (isMoney ? `￥${Math.round(value)}` : `${Math.round(value)}`),
          },
          splitLine: { lineStyle: { color: "#e2e8f0", type: "dashed" } },
        },
        series: [
          {
            name: title,
            type: "line",
            smooth: true,
            symbol: "circle",
            symbolSize: 8,
            data: points.map((item) => item.value),
            lineStyle: { width: 3, color },
            itemStyle: {
              color,
              borderWidth: 2,
              borderColor: "#fff",
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: `${color}33` },
                { offset: 1, color: `${color}08` },
              ]),
            },
          },
        ],
      });

      setter(chart);
    },
    renderPieChart(refName, title, list, setter) {
      const dom = this.$refs[refName];
      if (!dom) return;
      const chart = echarts.init(dom, "macarons");
      const points = this.toPoints(list);
      const colors = ["#3b82f6", "#22c55e", "#f59e0b", "#ef4444", "#8b5cf6", "#14b8a6", "#06b6d4", "#f97316"];

      chart.setOption({
        title: {
          text: title,
          left: "center",
          top: 8,
          textStyle: { fontSize: 14, color: "#334155" },
        },
        tooltip: {
          trigger: "item",
          backgroundColor: "rgba(37, 99, 235, 0.92)",
          borderColor: "rgba(37, 99, 235, 0.8)",
          textStyle: { color: "#fff" },
          formatter: "{a} <br/>{b}: {c} ({d}%)",
        },
        legend: {
          left: "center",
          bottom: 10,
          type: "scroll",
          textStyle: { color: "#64748b" },
        },
        series: [
          {
            name: title,
            type: "pie",
            radius: ["35%", "68%"],
            center: ["50%", "48%"],
            label: { color: "#334155" },
            itemStyle: {
              borderColor: "#fff",
              borderWidth: 2,
            },
            data: points.map((item, index) => ({
              name: item.name,
              value: item.value,
              itemStyle: { color: colors[index % colors.length] },
            })),
          },
        ],
      });

      setter(chart);
    },
    renderPlatformUsageChart(setter) {
      const dom = this.$refs.platformUsageChart;
      if (!dom) return;
      const chart = echarts.init(dom, "macarons");
      const data = [
        { name: "区县平台使用情况", value: Number(this.platformUsage.districtPlatformTotal || 0), color: "#3b82f6" },
        { name: "学校平台使用情况", value: Number(this.platformUsage.schoolPlatformTotal || 0), color: "#22c55e" },
        { name: "学生平台使用情况", value: Number(this.platformUsage.studentPlatformTotal || 0), color: "#f59e0b" },
      ];

      chart.setOption({
        title: {
          text: "平台使用情况",
          left: "center",
          top: 8,
          textStyle: { fontSize: 14, color: "#334155" },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "shadow" },
          backgroundColor: "rgba(2, 132, 199, 0.92)",
          borderColor: "rgba(2, 132, 199, 0.8)",
          textStyle: { color: "#fff" },
        },
        grid: { left: 20, right: 20, top: 60, bottom: 25, containLabel: true },
        xAxis: {
          type: "value",
          axisLabel: { color: "#64748b" },
          splitLine: { lineStyle: { color: "#e2e8f0", type: "dashed" } },
        },
        yAxis: {
          type: "category",
          data: data.map((item) => item.name),
          axisLabel: { color: "#64748b" },
        },
        series: [
          {
            name: "数量",
            type: "bar",
            barWidth: 24,
            data: data.map((item) => ({
              value: item.value,
              itemStyle: {
                borderRadius: [0, 8, 8, 0],
                color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                  { offset: 0, color: item.color },
                  { offset: 1, color: "#dbeafe" },
                ]),
              },
            })),
            label: { show: true, position: "right", color: "#334155" },
          },
        ],
      });

      setter(chart);
    },
    renderJyjSummaryChart(setter) {
      const dom = this.$refs.jyjSummaryChart;
      if (!dom) return;
      const chart = echarts.init(dom, "macarons");
      const data = [
        { name: "学生总数", value: Number(this.jyjSummary.studentTotal || 0), color: "#3b82f6" },
        { name: "学校总数", value: Number(this.jyjSummary.schoolTotal || 0), color: "#22c55e" },
        { name: "供应商总数", value: Number(this.jyjSummary.supplierTotal || 0), color: "#8b5cf6" },
        { name: "采购总金额", value: Number(this.jyjSummary.moneyTotal || 0), color: "#ef4444" },
      ];

      chart.setOption({
        title: {
          text: "教育局使用情况",
          left: "center",
          top: 8,
          textStyle: { fontSize: 14, color: "#334155" },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: { type: "shadow" },
          backgroundColor: "rgba(37, 99, 235, 0.92)",
          borderColor: "rgba(37, 99, 235, 0.8)",
          textStyle: { color: "#fff" },
          formatter: (params) => {
            const item = params && params[0];
            if (!item) return "";
            const prefix = item.name === "采购总金额" ? "￥" : "";
            return `${item.name}<br/>${prefix}${Number(item.value || 0).toLocaleString()}`;
          },
        },
        grid: { left: 20, right: 20, top: 60, bottom: 30, containLabel: true },
        xAxis: {
          type: "category",
          data: data.map((item) => item.name),
          axisLabel: { color: "#64748b" },
          axisLine: { lineStyle: { color: "#cbd5e1" } },
        },
        yAxis: {
          type: "value",
          axisLabel: { color: "#64748b" },
          splitLine: { lineStyle: { color: "#e2e8f0", type: "dashed" } },
        },
        series: [
          {
            name: "教育局使用情况",
            type: "bar",
            barWidth: "45%",
            data: data.map((item) => ({
              value: item.value,
              itemStyle: {
                borderRadius: [6, 6, 0, 0],
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: item.color },
                  { offset: 1, color: "#dbeafe" },
                ]),
              },
            })),
            label: { show: true, position: "top", color: "#334155" },
          },
        ],
      });

      setter(chart);
    },
  },
};
</script>

<style lang="scss" scoped>
.usage-dashboard {
  min-height: calc(100vh - 84px);
  background: linear-gradient(180deg, #f8fbff 0%, #f3f7ff 100%);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 16px;
  padding: 18px 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(34, 197, 94, 0.08), rgba(245, 158, 11, 0.08));
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.page-desc {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.filter-form {
  flex-shrink: 0;
}

.section-block {
  margin-top: 16px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.section-tip {
  margin-top: 6px;
  color: #64748b;
  font-size: 13px;
}

.metric-grid {
  margin-bottom: 0;
}

.metric-card,
.mini-card,
.neutral-card {
  border-radius: 18px;
  color: #fff;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
  padding: 18px 20px;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.metric-card.blue {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
}

.metric-card.green {
  background: linear-gradient(135deg, #16a34a, #34d399);
}

.metric-card.orange {
  background: linear-gradient(135deg, #f97316, #fb923c);
}

.metric-card.red {
  background: linear-gradient(135deg, #dc2626, #f87171);
}

.mini-card {
  background: linear-gradient(135deg, #0f172a, #1e293b);
}

.neutral-card {
  background: linear-gradient(135deg, #334155, #475569);
}

.metric-label,
.mini-title,
.neutral-title {
  font-size: 14px;
  opacity: 0.95;
}

.metric-value,
.mini-value,
.neutral-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
  margin: 10px 0 6px;
}

.metric-sub {
  font-size: 13px;
  opacity: 0.9;
}

.chart-grid {
  margin-top: 16px;
}

.chart-card {
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.card-header {
  font-weight: 700;
  color: #0f172a;
}

.chart-box {
  width: 100%;
  height: 360px;
}

.chart-box.large {
  height: 420px;
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .section-head {
    align-items: flex-start;
  }

  .chart-box.large {
    height: 360px;
  }
}
</style>
