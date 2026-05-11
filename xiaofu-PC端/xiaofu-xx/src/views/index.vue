<template>
  <div class="dashboard-container">
    <!-- 上半部分：统计数据和订购人数图表 -->
    <el-row :gutter="20" class="top-section">
      <!-- 左上：统计数据 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="stat-section-card">
          <div slot="header" class="clearfix">
            <span class="card-title">数据统计</span>
          </div>
          <div class="stats-container">
            <div class="stat-row">
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-user" style="color: #409eff"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">本年订购人数</div>
                  <div class="stat-value">{{ currentYearOrderCount }}</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-money" style="color: #67c23a"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">本年订购金额</div>
                  <div class="stat-value">¥{{ currentYearOrderAmount }}</div>
                </div>
              </div>
            </div>
            <div class="stat-row">
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-user" style="color: #e6a23c"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">去年订购人数</div>
                  <div class="stat-value">{{ lastYearOrderCount }}</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-money" style="color: #f56c6c"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">去年订购金额</div>
                  <div class="stat-value">¥{{ lastYearOrderAmount }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右上：订购人数柱状图 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"
              >本年各{{
                accountType === "city" ? "区县" : "学校"
              }}订购人数统计</span
            >
          </div>
          <div id="orderCountChart" style="width: 100%; height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下半部分：订购金额图表和厂商列表 -->
    <el-row :gutter="20" class="bottom-section">
      <!-- 左下：订购金额柱状图 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"
              >本年各{{
                accountType === "city" ? "区县" : "学校"
              }}订购金额统计</span
            >
          </div>
          <div id="orderAmountChart" style="width: 100%; height: 360px"></div>
        </el-card>
      </el-col>

      <!-- 右下：校服品类种类统计图 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title">校服品类种类统计</span>
          </div>
          <div id="uniformTypeChart" style="width: 100%; height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import * as echarts from "echarts";
export default {
  name: "Dashboard",
  data() {
    return {
      // 账户类型：city-市教育局，district-区县教育局
      accountType: "city", // 这里可以根据实际登录用户类型动态设置

      // 统计数据
      currentYearOrderCount: 0,
      currentYearOrderAmount: 0,
      lastYearOrderCount: 0,
      lastYearOrderAmount: 0,
      thisYearOrderAmount: [],
      // 图表实例
      orderCountChart: null,
      orderAmountChart: null,
      uniformTypeChart: null,

      // 校服品类统计
      uniformTypeData: [],
      thisYearOrderCount: [],
    };
  },
  mounted() {
    const orderCountValues = this.getOrderCountData();
    const orderAmountValues = this.getOrderAmountData();
    const categories = this.getChartCategories();
    this.thisYearOrderCount = categories.map((key, index) => ({ key, value: orderCountValues[index] }));
    this.thisYearOrderAmount = categories.map((key, index) => ({ key, value: orderAmountValues[index] }));
    this.uniformTypeData = [
      { key: "春秋装", value: 42 },
      { key: "夏装", value: 28 },
      { key: "冬装", value: 19 },
      { key: "礼服", value: 7 },
    ];
    this.currentYearOrderCount = orderCountValues.reduce((sum, value) => sum + value, 0);
    this.currentYearOrderAmount = orderAmountValues.reduce((sum, value) => sum + value, 0);
    this.lastYearOrderCount = Math.round(this.currentYearOrderCount * 0.85);
    this.lastYearOrderAmount = Math.round(this.currentYearOrderAmount * 0.82);
    this.initCharts();
  },
  beforeDestroy() {
    if (this.orderCountChart) {
      this.orderCountChart.dispose();
    }
    if (this.orderAmountChart) {
      this.orderAmountChart.dispose();
    }
    if (this.uniformTypeChart) {
      this.uniformTypeChart.dispose();
    }
  },
  methods: {
    // 初始化图表
    initCharts() {
      this.initOrderCountChart();
      this.initOrderAmountChart();
      this.initUniformTypeChart();
    },

    // 初始化订购人数图表
    initOrderCountChart() {
      const chartDom = document.getElementById("orderCountChart");
      if (!chartDom) return;

      this.orderCountChart = echarts.init(chartDom);

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80, //  像素值，用于为 dataZoom 提供充足空间
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 40, // 显示40%的数据，强制滚动条出现
            bottom: 30,
            height: 15, // 调整滚动条高度
          },
        ],
        xAxis: {
          type: "category",
          data: this.thisYearOrderCount.map((item) => item.key),
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 0,
            fontSize: 12,
            interval: 0,
          },
        },
        yAxis: {
          type: "value",
          name: "人数",
          nameTextStyle: {
            fontSize: 12,
          },
          axisLabel: {
            fontSize: 12,
          },
          minInterval: 1,
          splitLine: {
            show: true,
            lineStyle: {
              color: "#f0f0f0",
            },
          },
        },
        series: [
          {
            name: "订购人数",
            type: "bar",
            barWidth: "40%", // 调整柱状图宽度
            data: this.thisYearOrderCount.map((item) => item.value),
            itemStyle: {
              color: "#409EFF",
              borderRadius: [4, 4, 0, 0],
            },
            emphasis: {
              itemStyle: {
                color: "#66b1ff",
              },
            },
          },
        ],
      };

      this.orderCountChart.setOption(option);

      // 监听窗口大小变化
      window.addEventListener("resize", () => {
        if (this.orderCountChart) {
          this.orderCountChart.resize();
        }
      });
    },

    // 初始化订购金额图表
    initOrderAmountChart() {
      const chartDom = document.getElementById("orderAmountChart");
      if (!chartDom) return;

      this.orderAmountChart = echarts.init(chartDom);

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
          formatter: function (params) {
            return (
              params[0].name +
              "<br/>" +
              params[0].seriesName +
              ": ¥" +
              params[0].value.toLocaleString()
            );
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80, //  像素值，用于为 dataZoom 提供充足空间
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 40, // 显示40%的数据，强制滚动条出现
            bottom: 30,
            height: 15, // 调整滚动条高度
          },
        ],
        xAxis: {
          type: "category",
          data: this.thisYearOrderAmount.map((item) => item.key),
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: 0,
            fontSize: 12,
            interval: 0,
          },
        },
        yAxis: {
          type: "value",
          name: "金额(元)",
          nameTextStyle: {
            fontSize: 12,
          },
          axisLabel: {
            fontSize: 12,
            formatter: function (value) {
              return "¥" + Math.round(value);
            },
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: "#f0f0f0",
            },
          },
        },
        series: [
          {
            name: "订购金额",
            type: "bar",
            barWidth: "40%", // 调整柱状图宽度
            data: this.thisYearOrderAmount.map((item) => item.value),
            itemStyle: {
              color: "#67C23A",
              borderRadius: [4, 4, 0, 0],
            },
            emphasis: {
              itemStyle: {
                color: "#85ce61",
              },
            },
          },
        ],
      };

      this.orderAmountChart.setOption(option);

      // 监听窗口大小变化
      window.addEventListener("resize", () => {
        if (this.orderAmountChart) {
          this.orderAmountChart.resize();
        }
      });
    },

    // 初始化校服品类统计图表
    initUniformTypeChart() {
      const chartDom = document.getElementById("uniformTypeChart");
      if (!chartDom) return;

      this.uniformTypeChart = echarts.init(chartDom);

      const option = {
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b}: {c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          left: "left",
          top: "middle",
          textStyle: {
            fontSize: 12,
          },
        },
        series: [
          {
            name: "校服品类",
            type: "pie",
            radius: ["40%", "70%"],
            center: ["60%", "50%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
              borderColor: "#fff",
              borderWidth: 2,
            },
            label: {
              show: false,
              position: "center",
            },
            emphasis: {
              label: {
                show: true,
                fontSize: "16",
                fontWeight: "bold",
              },
            },
            labelLine: {
              show: false,
            },
            data: this.uniformTypeData.map((item, index) => ({
              value: item.value,
              name: item.key,
              itemStyle: {
                color: this.getChartColor(index),
              },
            })),
          },
        ],
      };

      this.uniformTypeChart.setOption(option);

      // 监听窗口大小变化
      window.addEventListener("resize", () => {
        if (this.uniformTypeChart) {
          this.uniformTypeChart.resize();
        }
      });
    },

    // 获取图表颜色
    getChartColor(index) {
      const colors = [
        "#409EFF",
        "#67C23A",
        "#E6A23C",
        "#F56C6C",
        "#909399",
        "#9C27B0",
        "#FF9800",
        "#4CAF50",
        "#2196F3",
        "#FF5722"
      ];
      return colors[index % colors.length];
    },

    // 获取图表分类数据
    getChartCategories() {
      if (this.accountType === "city") {
        // 市教育局看各区县
        return [
          "长安区",
          "桥西区",
          "新华区",
          "裕华区",
          "井陉矿区",
          "栾城区",
          "藁城区",
          "鹿泉区",
        ];
      } else {
        // 区县教育局看各学校
        return [
          "第一中学",
          "第二中学",
          "实验小学",
          "育才小学",
          "希望中学",
          "明德小学",
          "文华中学",
          "阳光小学",
        ];
      }
    },

    // 获取订购人数数据
    getOrderCountData() {
      if (this.accountType === "city") {
        return [1580, 1420, 1680, 1350, 890, 1120, 1450, 1280];
      } else {
        return [320, 280, 450, 380, 290, 350, 420, 310];
      }
    },

    // 获取订购金额数据
    getOrderAmountData() {
      if (this.accountType === "city") {
        return [316000, 284000, 336000, 270000, 178000, 224000, 290000, 256000];
      } else {
        return [64000, 56000, 90000, 76000, 58000, 70000, 84000, 62000];
      }
    },


    // 加载数据
    loadData() {
      // 这里可以调用API加载实际数据
      // 根据用户权限设置accountType
      // this.accountType = this.getUserAccountType()

      // 重新渲染图表
      this.$nextTick(() => {
        this.resizeCharts();

        if (this.orderCountChart) {
          this.orderCountChart.setOption({
            xAxis: {
              data: this.getChartCategories(),
            },
            series: [
              {
                data: this.getOrderCountData(),
              },
            ],
          });
        }

        if (this.orderAmountChart) {
          this.orderAmountChart.setOption({
            xAxis: {
              data: this.getChartCategories(),
            },
            series: [
              {
                data: this.getOrderAmountData(),
              },
            ],
          });
        }
      });
    },

    // 重新调整图表大小
    resizeCharts() {
      setTimeout(() => {
        if (this.orderCountChart) {
          this.orderCountChart.resize();
        }
        if (this.orderAmountChart) {
          this.orderAmountChart.resize();
        }
        if (this.uniformTypeChart) {
          this.uniformTypeChart.resize();
        }
      }, 200);
    },
  },
};
</script>

<style lang="scss" scoped>
.box-card-item {
  height: 400px;
}
.dashboard-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 84px);
  height: calc(100vh - 84px);
  display: flex;
  flex-direction: column;
}

.top-section {
  height: 48%;
  margin-bottom: 25px;
}

// 强制所有卡片高度完全一致
.stat-section-card,
.chart-card,
.manufacturer-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 100% !important;
  display: flex !important;
  flex-direction: column !important;
  border-radius: 8px;

  .el-card__header {
    height: 50px !important;
    min-height: 50px !important;
    max-height: 50px !important;
    padding: 15px 20px !important;
    border-bottom: 1px solid #ebeef5;
    display: flex !important;
    align-items: center !important;
    flex-shrink: 0 !important;
    background-color: #fafafa;
    box-sizing: border-box !important;
  }

  .el-card__body {
    flex: 1 !important;
    padding: 0 !important;
    display: flex !important;
    flex-direction: column !important;
    overflow: hidden !important;
    height: calc(100% - 50px) !important;
    box-sizing: border-box !important;
  }

  .card-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin: 0 !important;
    line-height: 1.2 !important;
  }
}

// 统计数据容器
.stats-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 30px;
}

.stat-row {
  flex: 1;
  display: flex;
  gap: 30px;
}

.stat-card {
  flex: 1;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 12px;
  padding: 25px 20px;
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  min-height: 100px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.12);
  }

  .stat-icon {
    font-size: 40px;
    margin-right: 20px;
    width: 60px;
    text-align: center;
    opacity: 0.8;
  }

  .stat-content {
    flex: 1;

    .stat-title {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
      font-weight: 500;
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      line-height: 1.2;
    }
  }
}

// 图表容器优化
.chart-card {
  .el-card__body {
    padding: 20px !important;
    overflow: visible !important;
  }
}

// 表格容器优化
.manufacturer-card {
  .el-card__body {
    padding: 0 !important;
  }
}

.manufacturer-table-container {
  flex: 1 !important;
  overflow: hidden !important;
  height: 100% !important;

  .el-table {
    height: 100% !important;
    border: none !important;
  }

  .el-table__header-wrapper {
    background-color: #fafafa;
  }

  .el-table__body-wrapper {
    max-height: none !important;
    height: calc(100% - 40px) !important;
    overflow-y: auto !important;
  }

  .el-table th {
    background-color: #fafafa !important;
    border-bottom: 1px solid #ebeef5 !important;
  }

  .el-table td {
    border-bottom: 1px solid #f5f5f5 !important;
  }
}

.dialog-header {
  margin-bottom: 20px;

  h3 {
    margin: 0;
    color: #333;
    font-size: 18px;
  }
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 1200px) {
  .dashboard-container {
    height: auto;
    min-height: calc(100vh - 84px);
  }

  .top-section,
  .bottom-section {
    height: auto;
    margin: 0 0 20px 0;
  }

  .stat-section-card,
  .chart-card,
  .manufacturer-card {
    height: 400px !important;
    min-height: 400px !important;
  }

  .stat-card {
    min-height: 80px;
    padding: 20px 15px;

    .stat-icon {
      font-size: 32px;
      width: 50px;
    }

    .stat-content {
      .stat-title {
        font-size: 12px;
      }

      .stat-value {
        font-size: 20px;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
    height: auto;
    min-height: calc(100vh - 84px);
  }

  .top-section,
  .bottom-section {
    height: auto;
    margin: 0 0 15px 0;
  }

  .top-section .el-col,
  .bottom-section .el-col {
    margin-bottom: 15px;
  }

  .stat-section-card,
  .chart-card,
  .manufacturer-card {
    height: 350px !important;
    min-height: 350px !important;
  }

  .stats-container {
    gap: 15px;
    padding: 15px;
  }

  .stat-row {
    gap: 15px;
  }

  .stat-card {
    min-height: 70px;
    padding: 15px 12px;

    .stat-icon {
      font-size: 28px;
      width: 45px;
      margin-right: 15px;
    }

    .stat-content {
      .stat-title {
        font-size: 11px;
      }

      .stat-value {
        font-size: 18px;
      }
    }
  }
}
</style>
