<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

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
      default: '300px'
    }
  },
  data() {
    return {
      chart: null
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

      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          },
          formatter: function (params) {
            const data = params[0];
            return data.name + '<br/>库存量: ' + data.value + '件<br/>安全库存: ' + data.data.safe + '件';
          },
          backgroundColor: 'rgba(61, 90, 254, 0.9)',
          borderColor: 'rgba(61, 90, 254, 0.8)',
          textStyle: {
            color: '#fff'
          }
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            interval: 0,
            rotate: 30,
            color: '#5c6b8a'
          },
          axisLine: {
            lineStyle: {
              color: '#eaedf7'
            }
          }
        }],
        yAxis: [{
          type: 'value',
          name: '库存量(件)',
          nameTextStyle: {
            color: '#5c6b8a'
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#5c6b8a'
          },
          axisLine: {
            lineStyle: {
              color: '#eaedf7'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#eaedf7',
              type: 'dashed'
            }
          }
        }],
        series: [{
          name: '当前库存',
          type: 'bar',
          barWidth: '60%',
          data: [
            { value: 110, safe: 50 },
            { value: 85, safe: 50 },
            { value: 65, safe: 50 },
            { value: 120, safe: 70 },
            { value: 95, safe: 70 },
            { value: 78, safe: 70 },
            { value: 58, safe: 40 }
          ],
          itemStyle: {
            normal: {
              color: function (params) {
                // 库存低于安全库存显示红色，否则显示蓝色
                return params.data.value < params.data.safe
                  ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#ff6e76' },
                    { offset: 1, color: '#f56c6c' }
                  ])
                  : new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#3459cc' },
                    { offset: 1, color: '#4073e9' }
                  ]);
              },
              borderRadius: [4, 4, 0, 0]
            }
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0,0,0,0.2)'
            }
          },
          label: {
            show: true,
            position: 'top',
            color: '#5c6b8a',
            fontSize: 12
          },
          markLine: {
            data: [
              {
                name: '平均值',
                type: 'average',
                lineStyle: {
                  color: '#91cc75',
                  type: 'dashed',
                  width: 2
                },
                label: {
                  color: '#5c6b8a',
                  fontSize: 12,
                  backgroundColor: 'rgba(255, 255, 255, 0.8)',
                  padding: [4, 8],
                  borderRadius: 4
                }
              }
            ]
          },
          animationDuration,
          animationEasing: 'elasticOut'
        }]
      })
    }
  }
}
</script>

<style>
.bar-chart {
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
