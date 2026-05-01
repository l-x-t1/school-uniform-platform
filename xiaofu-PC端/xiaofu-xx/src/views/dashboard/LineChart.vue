<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ expectedData, actualData } = {}) {
      this.chart.setOption({
        title: {
          text: '',
          subtext: ''
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: 'rgba(61, 90, 254, 0.9)'
            },
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.5)',
              width: 1.5
            }
          },
          padding: [8, 12],
          backgroundColor: 'rgba(61, 90, 254, 0.9)',
          borderColor: 'rgba(61, 90, 254, 0.8)',
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          data: ['计划', '实际'],
          bottom: 10,
          textStyle: {
            color: '#5c6b8a'
          },
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 20
        },
        grid: {
          left: 15,
          right: 15,
          bottom: 45,
          top: 35,
          containLabel: true
        },
        xAxis: {
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          boundaryGap: false,
          axisTick: {
            show: false
          },
          axisLabel: {
            margin: 10,
            color: '#5c6b8a'
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.2)'
            }
          },
          splitLine: {
            show: false
          }
        },
        yAxis: {
          name: '数量（件）',
          nameTextStyle: {
            color: '#5c6b8a',
            padding: [0, 0, 5, 0]
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.1)',
              type: 'dashed'
            }
          },
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.2)'
            }
          },
          axisLabel: {
            margin: 10,
            color: '#5c6b8a'
          }
        },
        series: [{
          name: '计划',
          smooth: true,
          type: 'line',
          symbolSize: 8,
          symbol: 'circle',
          itemStyle: {
            color: '#3459cc',
            borderWidth: 2
          },
          lineStyle: {
            width: 3,
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#3459cc' },
              { offset: 1, color: '#4073e9' }
            ]),
            shadowColor: 'rgba(61, 90, 254, 0.3)',
            shadowBlur: 10
          },
          emphasis: {
            itemStyle: {
              color: '#3459cc',
              borderColor: '#fff',
              borderWidth: 2,
              shadowColor: 'rgba(61, 90, 254, 0.5)',
              shadowBlur: 10
            }
          },
          data: expectedData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: '实际',
          smooth: true,
          type: 'line',
          symbolSize: 8,
          symbol: 'circle',
          itemStyle: {
            color: '#36cbcb',
            borderWidth: 2
          },
          lineStyle: {
            width: 3,
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#36cbcb' },
              { offset: 1, color: '#5c8ef9' }
            ]),
            shadowColor: 'rgba(54, 203, 203, 0.3)',
            shadowBlur: 10
          },
          emphasis: {
            itemStyle: {
              color: '#36cbcb',
              borderColor: '#fff',
              borderWidth: 2,
              shadowColor: 'rgba(54, 203, 203, 0.5)',
              shadowBlur: 10
            }
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(54, 203, 203, 0.3)' },
              { offset: 0.5, color: 'rgba(54, 203, 203, 0.1)' },
              { offset: 1, color: 'rgba(54, 203, 203, 0.02)' }
            ])
          },
          data: actualData,
          animationDuration: 2800,
          animationEasing: 'quadraticOut'
        }]
      })
    }
  }
}
</script>

<style>
.line-chart {
  position: relative;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(230, 235, 245, 0.9);
  backdrop-filter: blur(10px);

  .chart-wrapper {
    width: 100%;
    height: 100%;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background:
        radial-gradient(circle at 50% 0%, rgba(99, 102, 241, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 0% 50%, rgba(59, 130, 246, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 100% 50%, rgba(16, 185, 129, 0.1) 0%, transparent 50%);
      border-radius: 16px;
      z-index: 0;
    }

    .chart {
      width: 100%;
      height: 100%;
      position: relative;
      z-index: 1;
    }
  }

  :deep(.echarts) {
    width: 100%;
    height: 100%;
  }

  :deep(.echarts-tooltip) {
    background: rgba(255, 255, 255, 0.95) !important;
    border: 1px solid rgba(99, 102, 241, 0.2) !important;
    box-shadow:
      0 8px 24px rgba(0, 0, 0, 0.08),
      0 0 0 1px rgba(255, 255, 255, 0.2) !important;
    border-radius: 12px !important;
    backdrop-filter: blur(10px) !important;
    padding: 12px !important;
  }

  :deep(.echarts-legend) {
    padding: 12px !important;
    background: rgba(255, 255, 255, 0.9) !important;
    border-radius: 12px !important;
    box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.05),
      0 0 0 1px rgba(255, 255, 255, 0.2) !important;
    border: 1px solid rgba(230, 235, 245, 0.9) !important;
  }
}
</style>
