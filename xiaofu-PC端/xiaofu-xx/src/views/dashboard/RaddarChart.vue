<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 3000

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
          backgroundColor: 'rgba(61, 90, 254, 0.9)',
          borderColor: 'rgba(61, 90, 254, 0.8)',
          textStyle: {
            color: '#fff'
          }
        },
        radar: {
          radius: '70%',
          center: ['50%', '45%'],
          splitNumber: 5,
          shape: 'circle',
          splitArea: {
            areaStyle: {
              color: ['rgba(255, 255, 255, 0.05)', 'rgba(61, 90, 254, 0.05)'],
              shadowColor: 'rgba(0, 0, 0, 0.2)',
              shadowBlur: 10
            }
          },
          axisName: {
            color: '#5c6b8a',
            fontSize: 12
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.2)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(61, 90, 254, 0.2)'
            }
          },
          indicator: [
            { name: 'S码', max: 100 },
            { name: 'M码', max: 100 },
            { name: 'L码', max: 100 },
            { name: 'XL码', max: 100 },
            { name: 'XXL码', max: 100 },
            { name: '特制', max: 100 }
          ]
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['小学', '初中', '高中'],
          textStyle: {
            color: '#5c6b8a'
          },
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 20
        },
        series: [{
          type: 'radar',
          emphasis: {
            lineStyle: {
              width: 4
            }
          },
          data: [
            {
              value: [80, 65, 35, 10, 5, 20],
              name: '小学',
              symbolSize: 6,
              areaStyle: {
                color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                  { offset: 0, color: 'rgba(84, 112, 198, 0.8)' },
                  { offset: 1, color: 'rgba(84, 112, 198, 0.2)' }
                ])
              },
              lineStyle: {
                type: 'solid',
                width: 2,
                color: '#5470c6'
              },
              itemStyle: {
                color: '#5470c6'
              }
            },
            {
              value: [25, 55, 75, 35, 10, 15],
              name: '初中',
              symbolSize: 6,
              areaStyle: {
                color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                  { offset: 0, color: 'rgba(145, 204, 117, 0.8)' },
                  { offset: 1, color: 'rgba(145, 204, 117, 0.2)' }
                ])
              },
              lineStyle: {
                type: 'solid',
                width: 2,
                color: '#91cc75'
              },
              itemStyle: {
                color: '#91cc75'
              }
            },
            {
              value: [10, 25, 40, 70, 45, 20],
              name: '高中',
              symbolSize: 6,
              areaStyle: {
                color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                  { offset: 0, color: 'rgba(250, 200, 88, 0.8)' },
                  { offset: 1, color: 'rgba(250, 200, 88, 0.2)' }
                ])
              },
              lineStyle: {
                type: 'solid',
                width: 2,
                color: '#fac858'
              },
              itemStyle: {
                color: '#fac858'
              }
            }
          ],
          animationDuration: animationDuration
        }]
      })
    }
  }
}
</script>

<style>
.raddar-chart {
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
