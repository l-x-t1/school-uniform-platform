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
        setOptions({ amountData, moneyData, xAxisData } = {}) {
            this.chart.setOption({
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                            color: '#999'
                        }
                    }
                },
                toolbox: {
                    feature: {
                        dataView: { show: false, readOnly: false },
                        magicType: { show: false, type: ['line', 'bar'] },
                        restore: { show: false },
                        saveAsImage: { show: false }
                    }
                },
                legend: {
                    data: ['订单数量', '订单金额'],
                    textStyle: {
                        color: '#5c6b8a'
                    }
                },
                grid: {
                    top: '70',
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        data: xAxisData || ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级', '七年级'],
                        axisPointer: {
                            type: 'shadow'
                        },
                        axisLabel: {
                            color: '#5c6b8a',
                            interval: 0,
                            rotate: 30
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#eaedf7'
                            }
                        }
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        name: '数量(件)',
                        min: 0,
                        nameTextStyle: {
                            color: '#5c6b8a'
                        },
                        axisLabel: {
                            formatter: '{value}',
                            color: '#5c6b8a'
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#eaedf7'
                            }
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(61, 90, 254, 0.1)',
                                type: 'dashed'
                            }
                        }
                    },
                    {
                        type: 'value',
                        name: '金额(元)',
                        min: 0,
                        nameTextStyle: {
                            color: '#5c6b8a'
                        },
                        axisLabel: {
                            formatter: '{value}',
                            color: '#5c6b8a'
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#eaedf7'
                            }
                        },
                        splitLine: {
                            show: false
                        }
                    }
                ],
                series: [
                    {
                        name: '订单数量',
                        type: 'bar',
                        barWidth: '40%',
                        data: amountData || [45, 38, 42, 36, 40, 28, 35],
                        itemStyle: {
                            normal: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#3459cc' },
                                    { offset: 1, color: '#4073e9' }
                                ]),
                                borderRadius: [4, 4, 0, 0]
                            }
                        },
                        label: {
                            show: true,
                            position: 'top',
                            color: '#5c6b8a',
                            fontSize: 12
                        },
                        animationDuration: 2800,
                        animationEasing: 'cubicInOut'
                    },
                    {
                        name: '订单金额',
                        type: 'line',
                        smooth: true,
                        yAxisIndex: 1,
                        symbolSize: 8,
                        symbol: 'circle',
                        data: moneyData || [4500, 3800, 4200, 3600, 4000, 2800, 3500],
                        itemStyle: {
                            color: '#67C23A',
                            borderWidth: 2
                        },
                        lineStyle: {
                            width: 3,
                            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                { offset: 0, color: '#67C23A' },
                                { offset: 1, color: '#95d475' }
                            ]),
                            shadowColor: 'rgba(103, 194, 58, 0.3)',
                            shadowBlur: 10
                        },
                        emphasis: {
                            itemStyle: {
                                color: '#67C23A',
                                borderColor: '#fff',
                                borderWidth: 2,
                                shadowColor: 'rgba(103, 194, 58, 0.5)',
                                shadowBlur: 10
                            }
                        },
                        animationDuration: 2800,
                        animationEasing: 'quadraticOut'
                    }
                ]
            })
        }
    }
}
</script>
