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
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
          backgroundColor: 'rgba(61, 90, 254, 0.9)',
          borderColor: 'rgba(61, 90, 254, 0.8)',
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['夏季校服', '冬季校服', '运动服', '礼服', '其他服装'],
          textStyle: {
            color: '#5c6b8a'
          },
          itemWidth: 15,
          itemHeight: 15,
          itemGap: 20
        },
        series: [
          {
            name: '校服类型',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            selectedMode: 'single',
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              color: '#5c6b8a'
            },
            labelLine: {
              lineStyle: {
                color: '#ddd'
              },
              smooth: 0.2,
              length: 10,
              length2: 20
            },
            data: [
              {
                value: 3560,
                name: '夏季校服',
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#3459cc' },
                    { offset: 1, color: '#4073e9' }
                  ])
                }
              },
              {
                value: 2640,
                name: '冬季校服',
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#36cbcb' },
                    { offset: 1, color: '#5c8ef9' }
                  ])
                }
              },
              {
                value: 1290,
                name: '运动服',
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#55bffe' },
                    { offset: 1, color: '#5c8ef9' }
                  ])
                }
              },
              {
                value: 784,
                name: '礼服',
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#a05cbc' },
                    { offset: 1, color: '#9b8fe3' }
                  ])
                }
              },
              {
                value: 288,
                name: '其他服装',
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#fac858' },
                    { offset: 1, color: '#ffab2b' }
                  ])
                }
              }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>

<style>
.pie-chart {
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
