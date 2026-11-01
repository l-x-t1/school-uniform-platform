<template>
  <div
    v-loading="loading"
    class="dashboard-container"
    element-loading-text="正在加载数据..."
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)"
  >

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
            <span class="card-title">校服订购人数统计</span>
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
            <span class="card-title">校服订购金额统计</span>
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
import {
  getPersonMoney,
  getSchoolOrderCount,
  getSchoolOrderAmount,
  getSchoolUniformType,
} from "@/api/index";

export default {
  name: "Dashboard",
  data() {
    return {
      // 统计数据
      currentYearOrderCount: 0,
      currentYearOrderAmount: 0,
      lastYearOrderCount: 0,
      lastYearOrderAmount: 0,
      thisYearOrderAmount: [],
      thisYearOrderCount: [],

      // 图表实例
      orderCountChart: null,
      orderAmountChart: null,
      uniformTypeChart: null,

      // 校服品类统计
      uniformTypeData: [],

      // 加载状态
      loading: false,
    };
  },
  mounted() {
    this.loadData();
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
    // 获取人数金额统计数据
    getPersonMoney() {
      getPersonMoney().then((res) => {
        this.currentYearOrderCount = res.data.thisOrderYearCount;
        this.currentYearOrderAmount = res.data.thisMoneyYearCount;
        this.lastYearOrderCount = res.data.lastOrderYearCount;
        this.lastYearOrderAmount = res.data.lastMoneyYearCount;
      });
    },

    // 初始化图表
    initCharts() {
      this.initOrderCountChart();
      this.initOrderAmountChart();
      this.initUniformTypeChart();
    },

    // 初始化校服订购人数统计图表
    initOrderCountChart() {
      const chartDom = document.getElementById("orderCountChart");
      if (!chartDom) return;

      this.orderCountChart = echarts.init(chartDom);

      const orderCountData = Array.isArray(this.thisYearOrderCount)
        ? this.thisYearOrderCount
        : [];
      const xAxisData = orderCountData.map(
        (item) => item.key || item.name || "未知"
      );
      const seriesData = orderCountData.map(
        (item) => item.value || item.count || 0
      );

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
          formatter(params) {
            if (params && params.length > 0) {
              return (
                params[0].name +
                "<br/>" +
                params[0].seriesName +
                ": " +
                params[0].value +
                "人"
              );
            }
            return "";
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80,
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: orderCountData.length > 8,
            xAxisIndex: [0],
            start: 0,
            end: orderCountData.length > 8 ? 40 : 100,
            bottom: 30,
            height: 15,
          },
        ],
        xAxis: {
          type: "category",
          data: xAxisData,
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: xAxisData.length > 6 ? 45 : 0,
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
            name: "校服订购人数",
            type: "bar",
            barWidth: "40%",
            data: seriesData,
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

      window.addEventListener("resize", () => {
        if (this.orderCountChart) {
          this.orderCountChart.resize();
        }
      });
    },

    // 初始化校服订购金额统计图表
    initOrderAmountChart() {
      const chartDom = document.getElementById("orderAmountChart");
      if (!chartDom) return;

      this.orderAmountChart = echarts.init(chartDom);

      const orderAmountData = Array.isArray(this.thisYearOrderAmount)
        ? this.thisYearOrderAmount
        : [];
      const xAxisData = orderAmountData.map(
        (item) => item.key || item.name || "未知"
      );
      const seriesData = orderAmountData.map(
        (item) => item.value || item.amount || 0
      );

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
          formatter(params) {
            if (params && params.length > 0) {
              return (
                params[0].name +
                "<br/>" +
                params[0].seriesName +
                ": ¥" +
                params[0].value.toLocaleString()
              );
            }
            return "";
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80,
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: orderAmountData.length > 8,
            xAxisIndex: [0],
            start: 0,
            end: orderAmountData.length > 8 ? 40 : 100,
            bottom: 30,
            height: 15,
          },
        ],
        xAxis: {
          type: "category",
          data: xAxisData,
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: xAxisData.length > 6 ? 45 : 0,
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
            formatter(value) {
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
            name: "校服订购金额",
            type: "bar",
            barWidth: "40%",
            data: seriesData,
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
        "#FF5722",
      ];
      return colors[index % colors.length];
    },

    // 页面数据加载
    loadData() {
      this.loading = true;

      Promise.all([
        getSchoolOrderCount(),
        getSchoolOrderAmount(),
        getSchoolUniformType(),
      ])
        .then((res) => {
          this.thisYearOrderCount = res[0].data || [];
          this.thisYearOrderAmount = res[1].data || [];
          this.uniformTypeData = res[2].data || [];

          this.$nextTick(() => {
            this.initCharts();
          });
        })
        .catch(() => {
          this.$message.error("获取统计数据失败，请稍后重试");
          this.thisYearOrderCount = [];
          this.thisYearOrderAmount = [];
          this.uniformTypeData = [];
        })
        .finally(() => {
          this.loading = false;
        });

      this.getPersonMoney();
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
  position: relative;
}

.top-section {
  height: 48%;
  margin-bottom: 25px;
}

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

.chart-card {
  .el-card__body {
    padding: 20px !important;
    overflow: visible !important;
  }
}

.manufacturer-card {
  .el-card__body {
    padding: 0 !important;
  }
}

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
