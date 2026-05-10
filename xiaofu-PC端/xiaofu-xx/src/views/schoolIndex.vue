<template>
  <div class="dashboard-container">
    <!-- 鍔犺浇鐘舵€?-->
    <div v-if="loading" class="loading-container">
      <el-loading
        :loading="loading"
        text="姝ｅ湪鍔犺浇鏁版嵁..."
        spinner="el-icon-loading"
        background="rgba(0, 0, 0, 0.8)"
      />
    </div>

    <!-- 涓婂崐閮ㄥ垎锛氱粺璁℃暟鎹拰璁㈣喘浜烘暟鍥捐〃 -->
    <el-row :gutter="20" class="top-section">
      <!-- 宸︿笂锛氱粺璁℃暟鎹?-->
      <el-col :span="12" class="box-card-item">
        <el-card class="stat-section-card">
          <div slot="header" class="clearfix">
            <span class="card-title">鏁版嵁缁熻</span>
          </div>
          <div class="stats-container">
            <div class="stat-row">
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-user" style="color: #409eff"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">鏈勾璁㈣喘浜烘暟</div>
                  <div class="stat-value">{{ currentYearOrderCount }}</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-money" style="color: #67c23a"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">鏈勾璁㈣喘閲戦</div>
                  <div class="stat-value">楼{{ currentYearOrderAmount }}</div>
                </div>
              </div>
            </div>
            <div class="stat-row">
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-user" style="color: #e6a23c"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">鍘诲勾璁㈣喘浜烘暟</div>
                  <div class="stat-value">{{ lastYearOrderCount }}</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="el-icon-money" style="color: #f56c6c"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-title">鍘诲勾璁㈣喘閲戦</div>
                  <div class="stat-value">楼{{ lastYearOrderAmount }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 鍙充笂锛氳璐汉鏁版煴鐘跺浘 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"
              >鏍℃湇璁㈣喘浜烘暟缁熻</span
            >
          </div>
          <div id="orderCountChart" style="width: 100%; height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 涓嬪崐閮ㄥ垎锛氳璐噾棰濆浘琛ㄥ拰鍘傚晢鍒楄〃 -->
    <el-row :gutter="20" class="bottom-section">
      <!-- 宸︿笅锛氳璐噾棰濇煴鐘跺浘 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"
              >鏍℃湇璁㈣喘閲戦缁熻</span
            >
          </div>
          <div id="orderAmountChart" style="width: 100%; height: 360px"></div>
        </el-card>
      </el-col>

      <!-- 鍙充笅锛氭牎鏈嶅搧绫荤绫荤粺璁″浘 -->
      <el-col :span="12" class="box-card-item">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title">鏍℃湇鍝佺被绉嶇被缁熻</span>
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
      // 缁熻鏁版嵁
      currentYearOrderCount: 0,
      currentYearOrderAmount: 0,
      lastYearOrderCount: 0,
      lastYearOrderAmount: 0,
      thisYearOrderAmount: [],
      thisYearOrderCount: [],

      // 鍥捐〃瀹炰緥
      orderCountChart: null,
      orderAmountChart: null,
      uniformTypeChart: null,

      // 鏍℃湇鍝佺被缁熻
      uniformTypeData: [],

      // 鍔犺浇鐘舵€?
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
    // 鍒濆鍖栧浘琛?
    initCharts() {
      this.initOrderCountChart();
      this.initOrderAmountChart();
      this.initUniformTypeChart();
    },

    // 鍒濆鍖栨牎鏈嶈璐汉鏁扮粺璁″浘琛?
    initOrderCountChart() {
      const chartDom = document.getElementById("orderCountChart");
      if (!chartDom) return;

      this.orderCountChart = echarts.init(chartDom);

      // 澶勭悊鏁版嵁鏍煎紡锛屾敮鎸佸绉嶅瓧娈靛悕
      const orderCountData = Array.isArray(this.thisYearOrderCount) ? this.thisYearOrderCount : [];
      const xAxisData = orderCountData.map((item) => item.key || item.name || '鏈煡');
      const seriesData = orderCountData.map((item) => item.value || item.count || 0);

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
          formatter: function (params) {
            if (params && params.length > 0) {
              return params[0].name + "<br/>" + params[0].seriesName + ": " + params[0].value + "人";
            }
            return '';
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80, //  鍍忕礌鍊硷紝鐢ㄤ簬涓?dataZoom 鎻愪緵鍏呰冻绌洪棿
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: orderCountData.length > 8, // 鍙湁鏁版嵁瓒呰繃8鏉℃椂鎵嶆樉绀烘粴鍔ㄦ潯
            xAxisIndex: [0],
            start: 0,
            end: orderCountData.length > 8 ? 40 : 100, // 鏍规嵁鏁版嵁閲忚皟鏁存樉绀烘瘮渚?
            bottom: 30,
            height: 15, // 璋冩暣婊氬姩鏉￠珮搴?
          },
        ],
        xAxis: {
          type: "category",
          data: xAxisData,
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: xAxisData.length > 6 ? 45 : 0, // 鏁版嵁澶氭椂鏃嬭浆鏍囩
            fontSize: 12,
            interval: 0,
          },
        },
        yAxis: {
          type: "value",
          name: "浜烘暟",
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
            name: "鏍℃湇璁㈣喘浜烘暟",
            type: "bar",
            barWidth: "40%", // 璋冩暣鏌辩姸鍥惧搴?
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

      // 鐩戝惉绐楀彛澶у皬鍙樺寲
      window.addEventListener("resize", () => {
        if (this.orderCountChart) {
          this.orderCountChart.resize();
        }
      });
    },

    // 鍒濆鍖栨牎鏈嶈璐噾棰濈粺璁″浘琛?
    initOrderAmountChart() {
      const chartDom = document.getElementById("orderAmountChart");
      if (!chartDom) return;

      this.orderAmountChart = echarts.init(chartDom);

      // 澶勭悊鏁版嵁鏍煎紡锛屾敮鎸佸绉嶅瓧娈靛悕
      const orderAmountData = Array.isArray(this.thisYearOrderAmount) ? this.thisYearOrderAmount : [];
      const xAxisData = orderAmountData.map((item) => item.key || item.name || '鏈煡');
      const seriesData = orderAmountData.map((item) => item.value || item.amount || 0);

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
          formatter: function (params) {
            if (params && params.length > 0) {
              return (
                params[0].name +
                "<br/>" +
                params[0].seriesName +
                ": 楼" +
                params[0].value.toLocaleString()
              );
            }
            return '';
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: 80, //  鍍忕礌鍊硷紝鐢ㄤ簬涓?dataZoom 鎻愪緵鍏呰冻绌洪棿
          containLabel: true,
        },
        dataZoom: [
          {
            type: "slider",
            show: orderAmountData.length > 8, // 鍙湁鏁版嵁瓒呰繃8鏉℃椂鎵嶆樉绀烘粴鍔ㄦ潯
            xAxisIndex: [0],
            start: 0,
            end: orderAmountData.length > 8 ? 40 : 100, // 鏍规嵁鏁版嵁閲忚皟鏁存樉绀烘瘮渚?
            bottom: 30,
            height: 15, // 璋冩暣婊氬姩鏉￠珮搴?
          },
        ],
        xAxis: {
          type: "category",
          data: xAxisData,
          axisTick: {
            alignWithLabel: true,
          },
          axisLabel: {
            rotate: xAxisData.length > 6 ? 45 : 0, // 鏁版嵁澶氭椂鏃嬭浆鏍囩
            fontSize: 12,
            interval: 0,
          },
        },
        yAxis: {
          type: "value",
          name: "閲戦(鍏?)",
          nameTextStyle: {
            fontSize: 12,
          },
          axisLabel: {
            fontSize: 12,
            formatter: function (value) {
              return "楼" + Math.round(value);
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
            name: "鏍℃湇璁㈣喘閲戦",
            type: "bar",
            barWidth: "40%", // 璋冩暣鏌辩姸鍥惧搴?
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

      // 鐩戝惉绐楀彛澶у皬鍙樺寲
      window.addEventListener("resize", () => {
        if (this.orderAmountChart) {
          this.orderAmountChart.resize();
        }
      });
    },

    // 鍒濆鍖栨牎鏈嶅搧绫荤粺璁″浘琛?
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
            name: "鏍℃湇鍝佺被",
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

      // 鐩戝惉绐楀彛澶у皬鍙樺寲
      window.addEventListener("resize", () => {
        if (this.uniformTypeChart) {
          this.uniformTypeChart.resize();
        }
      });
    },

    // 鑾峰彇鍥捐〃棰滆壊
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


    // 椤甸潰鏁版嵁鍔犺浇
    loadData() {
      this.loading = true;
      this.thisYearOrderCount = [
        { key: "2026", value: 0 },
        { key: "2025", value: 0 },
        { key: "2024", value: 900 },
        { key: "2023", value: 0 },
        { key: "2022", value: 0 },
      ];
      this.thisYearOrderAmount = [
        { key: "2026", value: 0 },
        { key: "2025", value: 0 },
        { key: "2024", value: 387000 },
        { key: "2023", value: 0 },
        { key: "2022", value: 0 },
      ];
      this.uniformTypeData = [
        { key: "春秋装", value: 52 },
        { key: "夏装", value: 34 },
        { key: "冬装", value: 18 },
        { key: "礼服", value: 6 },
      ];
      this.currentYearOrderCount = this.thisYearOrderCount.reduce((sum, item) => sum + Number(item.value || 0), 0);
      this.currentYearOrderAmount = this.thisYearOrderAmount.reduce((sum, item) => sum + Number(item.value || 0), 0);
      this.lastYearOrderCount = Math.round(this.currentYearOrderCount * 0.85);
      this.lastYearOrderAmount = Math.round(this.currentYearOrderAmount * 0.82);
      this.$nextTick(() => {
        this.initCharts();
        this.loading = false;
      });
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

.loading-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.top-section {
  height: 48%;
  margin-bottom: 25px;
}

// 寮哄埗鎵€鏈夊崱鐗囬珮搴﹀畬鍏ㄤ竴鑷?
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

// 缁熻鏁版嵁瀹瑰櫒
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

// 鍥捐〃瀹瑰櫒浼樺寲
.chart-card {
  .el-card__body {
    padding: 20px !important;
    overflow: visible !important;
  }
}

// 琛ㄦ牸瀹瑰櫒浼樺寲
.manufacturer-card {
  .el-card__body {
    padding: 0 !important;
  }
}


// 鍝嶅簲寮忚璁?
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



