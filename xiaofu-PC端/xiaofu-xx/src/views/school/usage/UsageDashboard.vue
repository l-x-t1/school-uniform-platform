<template>
  <div v-loading="loading" class="app-container edu-supervision-page">
    <div class="page-header">
      <div>
        <div class="page-title">教育局监管统计</div>
        <div class="page-desc">
          在同一页面集中查看平台使用情况、资金流水统计、订单列表和订单详情。
        </div>
      </div>
      <el-tag :type="isCityLevel ? 'success' : 'warning'" size="medium">
        {{ scopeLabel }}
      </el-tag>
    </div>

    <el-alert
      :closable="false"
      class="scope-alert"
      show-icon
      type="info"
      :title="scopeAlertText"
    />

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="部门ID">
          <el-input
            v-model="queryParams.deptId"
            :disabled="!isCityLevel"
            clearable
            placeholder="市教育局可输入区县部门ID"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="流水号">
          <el-input
            v-model="queryParams.flowNo"
            clearable
            placeholder="请输入流水号"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="订单号">
          <el-input
            v-model="queryParams.orderNo"
            clearable
            placeholder="请输入订单号"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input
            v-model="queryParams.supplierName"
            clearable
            placeholder="请输入供应商名称"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-tabs v-model="activeTab" class="main-tabs">
      <el-tab-pane label="平台使用情况" name="overview">
        <div class="section-block">
          <div class="section-head">
            <div>
              <div class="section-title">监管总览</div>
              <div class="section-tip">展示当前监管范围内的平台、学校和教育局统计概览。</div>
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
                <div class="metric-label">区县平台使用数</div>
                <div class="metric-value">{{ platformUsage.districtPlatformTotal || 0 }}</div>
                <div class="metric-sub">当前监管范围内区县平台覆盖</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <div class="metric-card red">
                <div class="metric-label">学校 / 学生平台使用数</div>
                <div class="metric-value">{{ platformUsage.schoolPlatformTotal || 0 }}</div>
                <div class="metric-sub">学生平台 {{ platformUsage.studentPlatformTotal || 0 }}</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="16" class="chart-grid">
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">本年订购人数统计</div>
                <div ref="orderCountChart" class="chart-box" />
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">本年订购金额统计</div>
                <div ref="orderAmountChart" class="chart-box" />
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">平台使用情况</div>
                <div ref="platformUsageChart" class="chart-box" />
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="section-block">
          <div class="section-head">
            <div>
              <div class="section-title">教育局与学校情况</div>
              <div class="section-tip">保留原使用情况页中的学校统计、教育局统计和价格趋势分析。</div>
            </div>
          </div>

          <el-row :gutter="16" class="metric-grid compact-grid">
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="mini-card">
                <div class="mini-title">学校采购总金额</div>
                <div class="mini-value">￥{{ formatMoney(schoolSummary.moneyTotal) }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="mini-card">
                <div class="mini-title">学校采购笔数</div>
                <div class="mini-value">{{ schoolSummary.countTotal || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="mini-card">
                <div class="mini-title">学校采购人数</div>
                <div class="mini-value">{{ schoolSummary.studentTotal || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="mini-card">
                <div class="mini-title">班子 / 团队数</div>
                <div class="mini-value">{{ schoolSummary.teamCount || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="neutral-card">
                <div class="neutral-title">学生总数</div>
                <div class="neutral-value">{{ jyjSummary.studentTotal || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="neutral-card">
                <div class="neutral-title">学校总数</div>
                <div class="neutral-value">{{ jyjSummary.schoolTotal || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="neutral-card">
                <div class="neutral-title">供应商总数</div>
                <div class="neutral-value">{{ jyjSummary.supplierTotal || 0 }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="neutral-card">
                <div class="neutral-title">采购总金额</div>
                <div class="neutral-value">￥{{ formatMoney(jyjSummary.moneyTotal) }}</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="16" class="chart-grid">
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">学校近5年订购人数</div>
                <div ref="fiveYearCountChart" class="chart-box" />
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">学校近5年订购金额</div>
                <div ref="fiveYearMoneyChart" class="chart-box" />
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="8">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">校服品类统计</div>
                <div ref="uniformTypeChart" class="chart-box" />
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="16" class="chart-grid">
            <el-col :xs="24" :lg="10">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">教育局使用情况概览</div>
                <div ref="jyjSummaryChart" class="chart-box" />
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="14">
              <el-card class="chart-card" shadow="hover">
                <div slot="header" class="card-header">价格趋势分析</div>
                <div ref="priceTrendChart" class="chart-box large" />
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <el-tab-pane label="资金流水统计" name="finance">
        <div class="section-block">
          <div class="section-head">
            <div>
              <div class="section-title">资金统计与流水</div>
              <div class="section-tip">市教育局可查看全部，区县教育局默认仅查看本区县及所属学校。</div>
            </div>
          </div>

          <el-row :gutter="16" class="metric-grid compact-grid">
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card total">
                <div class="summary-label">当前页流水金额</div>
                <div class="summary-value">￥{{ formatMoney(financeSummary.amountTotal) }}</div>
                <div class="summary-extra">流水 {{ financeTotal }} 条</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card pending">
                <div class="summary-label">待到账流水</div>
                <div class="summary-value">{{ financeSummary.pendingCount }}</div>
                <div class="summary-extra">按当前列表统计</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card settled">
                <div class="summary-label">已到账流水</div>
                <div class="summary-value">{{ financeSummary.completedCount }}</div>
                <div class="summary-extra">状态成功的流水数</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card flow">
                <div class="summary-label">当前页结算金额</div>
                <div class="summary-value">￥{{ formatMoney(settlementSummary.applyAmountTotal) }}</div>
                <div class="summary-extra">结算 {{ settlementTotal }} 条</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card total">
                <div class="summary-label">待结算金额</div>
                <div class="summary-value">￥{{ formatMoney(settlementSummary.pendingAmount) }}</div>
                <div class="summary-extra">待结算 {{ settlementSummary.pendingCount }} 条</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card settled">
                <div class="summary-label">已结算金额</div>
                <div class="summary-value">￥{{ formatMoney(settlementSummary.settledAmount) }}</div>
                <div class="summary-extra">已结算 {{ settlementSummary.settledCount }} 条</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card pending">
                <div class="summary-label">学校数</div>
                <div class="summary-value">{{ distinctSchoolCount }}</div>
                <div class="summary-extra">当前页涉及学校</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="3">
              <div class="summary-card flow">
                <div class="summary-label">支付渠道数</div>
                <div class="summary-value">{{ distinctPayChannelCount }}</div>
                <div class="summary-extra">当前页涉及渠道</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="16" class="table-grid">
            <el-col :xs="24" :xl="14">
              <el-card shadow="never" class="inner-card">
                <div slot="header" class="inner-card-header">资金流水列表</div>
                <el-table :data="financeFlowList" border height="420">
                  <el-table-column label="流水号" prop="flowNo" min-width="170" show-overflow-tooltip />
                  <el-table-column label="订单号" prop="orderNo" min-width="150" show-overflow-tooltip />
                  <el-table-column label="业务类型" width="100" align="center">
                    <template slot-scope="scope">{{ formatBusinessType(scope.row.businessType) }}</template>
                  </el-table-column>
                  <el-table-column label="金额" width="110" align="right">
                    <template slot-scope="scope">￥{{ formatMoney(scope.row.amount) }}</template>
                  </el-table-column>
                  <el-table-column label="收支方向" width="100" align="center">
                    <template slot-scope="scope">{{ formatFlowDirection(scope.row.flowDirection) }}</template>
                  </el-table-column>
                  <el-table-column label="收款方" prop="payeeName" min-width="140" show-overflow-tooltip />
                  <el-table-column label="状态" width="90" align="center">
                    <template slot-scope="scope">
                      <el-tag size="mini" :type="flowStatusType(scope.row.status)">
                        {{ formatFlowStatus(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="交易时间" width="120" align="center">
                    <template slot-scope="scope">{{ parseDate(scope.row.tradeTime) }}</template>
                  </el-table-column>
                </el-table>
                <pagination
                  v-show="financeTotal > 0"
                  :total="financeTotal"
                  :page.sync="financeQuery.pageNum"
                  :limit.sync="financeQuery.pageSize"
                  @pagination="loadFinanceData"
                />
              </el-card>
            </el-col>

            <el-col :xs="24" :xl="10">
              <el-card shadow="never" class="inner-card">
                <div slot="header" class="inner-card-header">结算统计</div>
                <el-table :data="settlementManageList" border height="420">
                  <el-table-column label="申请单号" prop="withdrawNo" min-width="170" show-overflow-tooltip />
                  <el-table-column label="供应商" prop="supplierName" min-width="140" show-overflow-tooltip />
                  <el-table-column label="结算金额" width="110" align="right">
                    <template slot-scope="scope">￥{{ formatMoney(scope.row.applyAmount) }}</template>
                  </el-table-column>
                  <el-table-column label="状态" width="90" align="center">
                    <template slot-scope="scope">
                      <el-tag size="mini" :type="settlementStatusType(scope.row.status)">
                        {{ formatSettlementStatus(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="申请时间" width="120" align="center">
                    <template slot-scope="scope">{{ parseDate(scope.row.applyTime) }}</template>
                  </el-table-column>
                </el-table>
                <pagination
                  v-show="settlementTotal > 0"
                  :total="settlementTotal"
                  :page.sync="settlementQuery.pageNum"
                  :limit.sync="settlementQuery.pageSize"
                  @pagination="loadSettlementData"
                />
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <el-tab-pane label="订单列表" name="orders">
        <div class="section-block">
          <div class="section-head">
            <div>
              <div class="section-title">订单监管列表</div>
              <div class="section-tip">点击“查看详情”可切换到订单详情页签，并自动带入订单ID。</div>
            </div>
          </div>

          <el-table :data="orderInfoList" border>
            <el-table-column label="订单号" prop="orderNo" min-width="170" show-overflow-tooltip />
            <el-table-column label="学校" prop="schoolName" min-width="150" show-overflow-tooltip />
            <el-table-column label="供应商" prop="supplierName" min-width="150" show-overflow-tooltip />
            <el-table-column label="学生" prop="studentName" width="100" align="center" />
            <el-table-column label="订单类型" width="100" align="center">
              <template slot-scope="scope">{{ formatOrderType(scope.row.orderType) }}</template>
            </el-table-column>
            <el-table-column label="支付金额" width="110" align="right">
              <template slot-scope="scope">￥{{ formatMoney(scope.row.payAmount) }}</template>
            </el-table-column>
            <el-table-column label="支付状态" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="payStatusType(scope.row.payStatus)">
                  {{ formatPayStatus(scope.row.payStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="订单状态" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="orderStatusType(scope.row.orderStatus)">
                  {{ formatOrderStatus(scope.row.orderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="支付时间" width="120" align="center">
              <template slot-scope="scope">{{ parseDate(scope.row.payTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="openOrderDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="orderTotal > 0"
            :total="orderTotal"
            :page.sync="orderQuery.pageNum"
            :limit.sync="orderQuery.pageSize"
            @pagination="loadOrderData"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="订单详情" name="orderItems">
        <div class="section-block">
          <div class="section-head">
            <div>
              <div class="section-title">订单详情监管</div>
              <div class="section-tip">支持按订单ID或商品名称过滤，默认展示当前选中订单的明细。</div>
            </div>
          </div>

          <el-row :gutter="16" class="selected-order-row">
            <el-col :xs="24" :lg="10">
              <el-card shadow="never" class="selected-order-card">
                <div slot="header" class="inner-card-header">当前选中订单</div>
                <div v-if="selectedOrder.id" class="selected-order-content">
                  <div>订单号：{{ selectedOrder.orderNo || '-' }}</div>
                  <div>学校：{{ selectedOrder.schoolName || '-' }}</div>
                  <div>供应商：{{ selectedOrder.supplierName || '-' }}</div>
                  <div>支付金额：￥{{ formatMoney(selectedOrder.payAmount) }}</div>
                  <div>订单状态：{{ formatOrderStatus(selectedOrder.orderStatus) }}</div>
                </div>
                <div v-else class="empty-text">请先在“订单列表”中点击“查看详情”。</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="14">
              <el-form :inline="true" :model="orderItemQuery" size="small" class="item-filter" @submit.native.prevent>
                <el-form-item label="订单ID">
                  <el-input v-model="orderItemQuery.orderId" clearable placeholder="请输入订单ID" @keyup.enter.native="loadOrderItemData" />
                </el-form-item>
                <el-form-item label="商品名称">
                  <el-input v-model="orderItemQuery.goodsName" clearable placeholder="请输入商品名称" @keyup.enter.native="loadOrderItemData" />
                </el-form-item>
                <el-form-item label="销售类型">
                  <el-select v-model="orderItemQuery.saleType" clearable placeholder="全部">
                    <el-option label="统购" value="1" />
                    <el-option label="零售" value="2" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="el-icon-search" @click="loadOrderItemData">查询</el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>

          <el-table :data="orderItemList" border>
            <el-table-column label="订单ID" prop="orderId" width="100" align="center" />
            <el-table-column label="商品名称" prop="goodsName" min-width="160" show-overflow-tooltip />
            <el-table-column label="尺码" prop="sizeName" width="110" align="center" />
            <el-table-column label="销售类型" width="90" align="center">
              <template slot-scope="scope">{{ formatSaleType(scope.row.saleType) }}</template>
            </el-table-column>
            <el-table-column label="单价" width="100" align="right">
              <template slot-scope="scope">￥{{ formatMoney(scope.row.unitPrice) }}</template>
            </el-table-column>
            <el-table-column label="数量" prop="buyNum" width="80" align="center" />
            <el-table-column label="行金额" width="110" align="right">
              <template slot-scope="scope">￥{{ formatMoney(scope.row.itemAmount) }}</template>
            </el-table-column>
            <el-table-column label="归属部门" prop="deptId" width="110" align="center" />
            <el-table-column label="备注" prop="remark" min-width="120" show-overflow-tooltip />
          </el-table>
          <pagination
            v-show="orderItemTotal > 0"
            :total="orderItemTotal"
            :page.sync="orderItemQuery.pageNum"
            :limit.sync="orderItemQuery.pageSize"
            @pagination="loadOrderItemData"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
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
  getSchoolUniformType
} from '@/api/index'
import { listFinanceFlow } from '@/api/school/financeFlow'
import { listOrderInfo } from '@/api/school/orderInfo'
import { listOrderItem } from '@/api/school/orderItem'
import { listSettlementManage } from '@/api/school/settlementManage'

export default {
  name: 'UsageDashboard',
  data() {
    return {
      activeTab: 'overview',
      loading: false,
      queryParams: {
        deptId: '',
        flowNo: '',
        orderNo: '',
        supplierName: ''
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
      financeFlowList: [],
      financeTotal: 0,
      settlementManageList: [],
      settlementTotal: 0,
      orderInfoList: [],
      orderTotal: 0,
      orderItemList: [],
      orderItemTotal: 0,
      selectedOrder: {},
      financeQuery: {
        pageNum: 1,
        pageSize: 8
      },
      settlementQuery: {
        pageNum: 1,
        pageSize: 6
      },
      orderQuery: {
        pageNum: 1,
        pageSize: 8
      },
      orderItemQuery: {
        pageNum: 1,
        pageSize: 8,
        orderId: undefined,
        goodsName: '',
        saleType: ''
      },
      orderCountChart: null,
      orderAmountChart: null,
      fiveYearCountChart: null,
      fiveYearMoneyChart: null,
      uniformTypeChart: null,
      platformUsageChart: null,
      jyjSummaryChart: null,
      priceTrendChart: null,
      resizeHandler: null
    }
  },
  computed: {
    roleKeys() {
      return this.$store.getters.roles || []
    },
    currentDeptId() {
      return this.$store.getters.deptId
    },
    isCityLevel() {
      return this.roleKeys.includes('admin') || String(this.currentDeptId) === '100'
    },
    scopeLabel() {
      return this.isCityLevel ? '市教育局：查看全部' : '区县教育局：仅查看本区县和所属学校'
    },
    scopeAlertText() {
      return this.isCityLevel
        ? '当前账号为市教育局，默认可查看全部监管数据。'
        : `当前账号为区县教育局，已自动锁定部门范围为 ${this.currentDeptId || '-'}。`
    },
    financeSummary() {
      return this.financeFlowList.reduce(
        (summary, item) => {
          const amount = Number(item.amount || 0)
          summary.amountTotal += amount
          if (String(item.status) === '1') {
            summary.completedCount += 1
          } else {
            summary.pendingCount += 1
          }
          return summary
        },
        {
          amountTotal: 0,
          completedCount: 0,
          pendingCount: 0
        }
      )
    },
    settlementSummary() {
      return this.settlementManageList.reduce(
        (summary, item) => {
          const amount = Number(item.applyAmount || 0)
          summary.applyAmountTotal += amount
          if (String(item.status) === '3') {
            summary.settledAmount += amount
            summary.settledCount += 1
          } else {
            summary.pendingAmount += amount
            summary.pendingCount += 1
          }
          return summary
        },
        {
          applyAmountTotal: 0,
          settledAmount: 0,
          settledCount: 0,
          pendingAmount: 0,
          pendingCount: 0
        }
      )
    },
    distinctSchoolCount() {
      return new Set(this.orderInfoList.map((item) => item.schoolId).filter(Boolean)).size
    },
    distinctPayChannelCount() {
      return new Set(this.financeFlowList.map((item) => item.payChannel).filter(Boolean)).size
    }
  },
  created() {
    if (!this.isCityLevel) {
      this.queryParams.deptId = this.currentDeptId
    }
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
        this.priceTrendChart
      ].forEach((chart) => chart && chart.resize())
    }
    window.addEventListener('resize', this.resizeHandler)
    this.loadAll()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeHandler)
    this.disposeCharts()
  },
  methods: {
    resolveDeptId() {
      if (this.isCityLevel) {
        return this.queryParams.deptId || undefined
      }
      return this.currentDeptId || undefined
    },
    buildScopeQuery(extra = {}) {
      const deptId = this.resolveDeptId()
      return deptId ? { ...extra, deptId } : { ...extra }
    },
    extractData(response, fallback) {
      if (!response) return fallback
      if (response.data !== undefined) return response.data
      if (response.rows !== undefined) return response.rows
      return fallback
    },
    formatMoney(value) {
      const num = Number(value || 0)
      return Number.isFinite(num)
        ? num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
        : '0.00'
    },
    parseDate(value) {
      return this.parseTime ? this.parseTime(value, '{y}-{m}-{d}') || '-' : value || '-'
    },
    toPoints(list) {
      return (Array.isArray(list) ? list : []).map((item) => ({
        name: item.key || item.name || item.label || '未知',
        value: Number(item.value ?? item.count ?? item.amount ?? 0)
      }))
    },
    formatBusinessType(value) {
      return (
        {
          1: '结算',
          2: '退款',
          3: '充值',
          4: '采购'
        }[value] || value || '-'
      )
    },
    formatFlowDirection(value) {
      return (
        {
          1: '收入',
          2: '支出'
        }[value] || value || '-'
      )
    },
    formatFlowStatus(value) {
      return (
        {
          0: '处理中',
          1: '已到账',
          2: '已转供应商',
          3: '失败'
        }[value] || value || '-'
      )
    },
    flowStatusType(value) {
      return (
        {
          0: 'warning',
          1: 'success',
          2: 'primary',
          3: 'danger'
        }[value] || 'info'
      )
    },
    formatOrderType(value) {
      return (
        {
          1: '统购',
          2: '零售'
        }[value] || value || '-'
      )
    },
    formatPayStatus(value) {
      return (
        {
          0: '未支付',
          1: '已支付',
          2: '已退款'
        }[value] || value || '-'
      )
    },
    payStatusType(value) {
      return (
        {
          0: 'warning',
          1: 'success',
          2: 'info'
        }[value] || 'info'
      )
    },
    formatOrderStatus(value) {
      return (
        {
          0: '待处理',
          1: '进行中',
          2: '已完成',
          3: '已取消'
        }[value] || value || '-'
      )
    },
    orderStatusType(value) {
      return (
        {
          0: 'warning',
          1: 'primary',
          2: 'success',
          3: 'info'
        }[value] || 'info'
      )
    },
    formatSettlementStatus(value) {
      return (
        {
          1: '待结算',
          2: '审核中',
          3: '已结算'
        }[value] || value || '-'
      )
    },
    settlementStatusType(value) {
      return (
        {
          1: 'warning',
          2: 'primary',
          3: 'success'
        }[value] || 'info'
      )
    },
    formatSaleType(value) {
      return (
        {
          1: '统购',
          2: '零售'
        }[value] || value || '-'
      )
    },
    handleQuery() {
      this.financeQuery.pageNum = 1
      this.settlementQuery.pageNum = 1
      this.orderQuery.pageNum = 1
      this.orderItemQuery.pageNum = 1
      this.loadAll()
    },
    resetQuery() {
      this.queryParams = {
        deptId: this.isCityLevel ? '' : this.currentDeptId,
        flowNo: '',
        orderNo: '',
        supplierName: ''
      }
      this.orderItemQuery = {
        pageNum: 1,
        pageSize: 8,
        orderId: this.selectedOrder.id || undefined,
        goodsName: '',
        saleType: ''
      }
      this.handleQuery()
    },
    async loadAll() {
      this.loading = true
      try {
        await Promise.all([
          this.loadOverviewData(),
          this.loadFinanceData(),
          this.loadSettlementData(),
          this.loadOrderData(),
          this.loadOrderItemData()
        ])
      } finally {
        this.loading = false
      }
    },
    async loadOverviewData() {
      const query = this.buildScopeQuery()
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
        getJyjTopList(query)
      ])

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
        jyjSummaryRes
      ] = tasks

      this.peopleStats = peopleRes.status === 'fulfilled' ? this.extractData(peopleRes.value, {}) : {}
      this.currentYearCountList = countRes.status === 'fulfilled' ? this.extractData(countRes.value, []) : []
      this.currentYearMoneyList = moneyRes.status === 'fulfilled' ? this.extractData(moneyRes.value, []) : []
      this.schoolFiveYearCountList =
        fiveYearCountRes.status === 'fulfilled' ? this.extractData(fiveYearCountRes.value, []) : []
      this.schoolFiveYearMoneyList =
        fiveYearMoneyRes.status === 'fulfilled' ? this.extractData(fiveYearMoneyRes.value, []) : []
      this.uniformTypeList = uniformTypeRes.status === 'fulfilled' ? this.extractData(uniformTypeRes.value, []) : []
      this.platformUsage = usageRes.status === 'fulfilled' ? this.extractData(usageRes.value, {}) : {}
      this.priceTrendList = priceTrendRes.status === 'fulfilled' ? this.extractData(priceTrendRes.value, []) : []
      this.schoolSummary = schoolSummaryRes.status === 'fulfilled' ? this.extractData(schoolSummaryRes.value, {}) : {}
      this.jyjSummary = jyjSummaryRes.status === 'fulfilled' ? this.extractData(jyjSummaryRes.value, {}) : {}

      this.$nextTick(() => {
        this.renderCharts()
      })
    },
    async loadFinanceData() {
      const response = await listFinanceFlow(
        this.buildScopeQuery({
          ...this.financeQuery,
          flowNo: this.queryParams.flowNo || undefined,
          orderNo: this.queryParams.orderNo || undefined
        })
      )
      this.financeFlowList = response.rows || []
      this.financeTotal = response.total || 0
    },
    async loadSettlementData() {
      const response = await listSettlementManage(
        this.buildScopeQuery({
          ...this.settlementQuery,
          supplierName: this.queryParams.supplierName || undefined
        })
      )
      this.settlementManageList = response.rows || []
      this.settlementTotal = response.total || 0
    },
    async loadOrderData() {
      const response = await listOrderInfo(
        this.buildScopeQuery({
          ...this.orderQuery,
          orderNo: this.queryParams.orderNo || undefined,
          supplierName: this.queryParams.supplierName || undefined
        })
      )
      this.orderInfoList = response.rows || []
      this.orderTotal = response.total || 0
    },
    async loadOrderItemData() {
      const response = await listOrderItem(
        this.buildScopeQuery({
          ...this.orderItemQuery,
          orderId: this.orderItemQuery.orderId || undefined,
          goodsName: this.orderItemQuery.goodsName || undefined,
          saleType: this.orderItemQuery.saleType || undefined
        })
      )
      this.orderItemList = response.rows || []
      this.orderItemTotal = response.total || 0
    },
    openOrderDetail(row) {
      this.selectedOrder = row || {}
      this.orderItemQuery.pageNum = 1
      this.orderItemQuery.orderId = row && row.id ? row.id : undefined
      this.activeTab = 'orderItems'
      this.loadOrderItemData()
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
        this.priceTrendChart
      ].forEach((chart) => chart && chart.dispose())

      this.orderCountChart = null
      this.orderAmountChart = null
      this.fiveYearCountChart = null
      this.fiveYearMoneyChart = null
      this.uniformTypeChart = null
      this.platformUsageChart = null
      this.jyjSummaryChart = null
      this.priceTrendChart = null
    },
    renderCharts() {
      this.disposeCharts()

      this.renderBarChart('orderCountChart', '本年订购人数统计', this.currentYearCountList, {
        color: '#3b82f6',
        yName: '人数'
      }, (chart) => {
        this.orderCountChart = chart
      })

      this.renderBarChart('orderAmountChart', '本年订购金额统计', this.currentYearMoneyList, {
        color: '#22c55e',
        yName: '金额',
        isMoney: true
      }, (chart) => {
        this.orderAmountChart = chart
      })

      this.renderLineChart('fiveYearCountChart', '学校近5年订购人数', this.schoolFiveYearCountList, '人数', '#6366f1', (chart) => {
        this.fiveYearCountChart = chart
      })

      this.renderLineChart('fiveYearMoneyChart', '学校近5年订购金额', this.schoolFiveYearMoneyList, '金额', '#f59e0b', (chart) => {
        this.fiveYearMoneyChart = chart
      }, true)

      this.renderPieChart('uniformTypeChart', '校服品类统计', this.uniformTypeList, (chart) => {
        this.uniformTypeChart = chart
      })

      this.renderPlatformUsageChart((chart) => {
        this.platformUsageChart = chart
      })

      this.renderJyjSummaryChart((chart) => {
        this.jyjSummaryChart = chart
      })

      this.renderLineChart('priceTrendChart', '价格趋势分析', this.priceTrendList, '金额', '#ef4444', (chart) => {
        this.priceTrendChart = chart
      }, true)
    },
    renderBarChart(refName, title, list, style, setter) {
      const dom = this.$refs[refName]
      if (!dom) return
      const chart = echarts.init(dom, 'macarons')
      const points = this.toPoints(list)

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: (params) => {
            const item = params && params[0]
            if (!item) return ''
            return `${item.name}<br/>${style.isMoney ? '￥' : ''}${this.formatMoney(item.value)}`
          }
        },
        grid: { left: 20, right: 20, top: 20, bottom: 40, containLabel: true },
        xAxis: {
          type: 'category',
          data: points.map((item) => item.name),
          axisLabel: { interval: 0, rotate: points.length > 6 ? 25 : 0 }
        },
        yAxis: {
          type: 'value',
          name: style.yName
        },
        series: [
          {
            name: title,
            type: 'bar',
            barWidth: '45%',
            data: points.map((item) => item.value),
            itemStyle: {
              borderRadius: [6, 6, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: style.color },
                { offset: 1, color: '#dbeafe' }
              ])
            }
          }
        ]
      })
      setter(chart)
    },
    renderLineChart(refName, title, list, yName, color, setter, isMoney = false) {
      const dom = this.$refs[refName]
      if (!dom) return
      const chart = echarts.init(dom, 'macarons')
      const points = this.toPoints(list)

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const item = params && params[0]
            if (!item) return ''
            return `${item.name}<br/>${isMoney ? '￥' : ''}${this.formatMoney(item.value)}`
          }
        },
        grid: { left: 20, right: 20, top: 20, bottom: 40, containLabel: true },
        xAxis: {
          type: 'category',
          data: points.map((item) => item.name),
          axisLabel: { interval: 0, rotate: points.length > 6 ? 25 : 0 }
        },
        yAxis: {
          type: 'value',
          name: yName
        },
        series: [
          {
            name: title,
            type: 'line',
            smooth: true,
            symbolSize: 8,
            data: points.map((item) => item.value),
            lineStyle: { color, width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: `${color}33` },
                { offset: 1, color: `${color}08` }
              ])
            }
          }
        ]
      })
      setter(chart)
    },
    renderPieChart(refName, title, list, setter) {
      const dom = this.$refs[refName]
      if (!dom) return
      const chart = echarts.init(dom, 'macarons')
      const points = this.toPoints(list)
      const colors = ['#3b82f6', '#22c55e', '#f59e0b', '#ef4444', '#8b5cf6', '#14b8a6']

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          bottom: 0,
          type: 'scroll'
        },
        series: [
          {
            type: 'pie',
            radius: ['36%', '68%'],
            center: ['50%', '44%'],
            data: points.map((item, index) => ({
              name: item.name,
              value: item.value,
              itemStyle: { color: colors[index % colors.length] }
            }))
          }
        ]
      })
      setter(chart)
    },
    renderPlatformUsageChart(setter) {
      const dom = this.$refs.platformUsageChart
      if (!dom) return
      const chart = echarts.init(dom, 'macarons')
      const data = [
        { name: '区县平台', value: Number(this.platformUsage.districtPlatformTotal || 0), color: '#3b82f6' },
        { name: '学校平台', value: Number(this.platformUsage.schoolPlatformTotal || 0), color: '#22c55e' },
        { name: '学生平台', value: Number(this.platformUsage.studentPlatformTotal || 0), color: '#f59e0b' }
      ]

      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }},
        grid: { left: 20, right: 20, top: 20, bottom: 20, containLabel: true },
        xAxis: { type: 'value' },
        yAxis: { type: 'category', data: data.map((item) => item.name) },
        series: [
          {
            type: 'bar',
            barWidth: 22,
            data: data.map((item) => ({
              value: item.value,
              itemStyle: {
                borderRadius: [0, 8, 8, 0],
                color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                  { offset: 0, color: item.color },
                  { offset: 1, color: '#dbeafe' }
                ])
              }
            }))
          }
        ]
      })
      setter(chart)
    },
    renderJyjSummaryChart(setter) {
      const dom = this.$refs.jyjSummaryChart
      if (!dom) return
      const chart = echarts.init(dom, 'macarons')
      const data = [
        { name: '学生总数', value: Number(this.jyjSummary.studentTotal || 0), color: '#3b82f6' },
        { name: '学校总数', value: Number(this.jyjSummary.schoolTotal || 0), color: '#22c55e' },
        { name: '供应商总数', value: Number(this.jyjSummary.supplierTotal || 0), color: '#8b5cf6' },
        { name: '采购总金额', value: Number(this.jyjSummary.moneyTotal || 0), color: '#ef4444' }
      ]

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: (params) => {
            const item = params && params[0]
            if (!item) return ''
            return `${item.name}<br/>${item.name === '采购总金额' ? '￥' : ''}${this.formatMoney(item.value)}`
          }
        },
        grid: { left: 20, right: 20, top: 20, bottom: 40, containLabel: true },
        xAxis: { type: 'category', data: data.map((item) => item.name) },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'bar',
            barWidth: '45%',
            data: data.map((item) => ({
              value: item.value,
              itemStyle: {
                borderRadius: [6, 6, 0, 0],
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: item.color },
                  { offset: 1, color: '#dbeafe' }
                ])
              }
            }))
          }
        ]
      })
      setter(chart)
    }
  }
}
</script>

<style lang="scss" scoped>
.edu-supervision-page {
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
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
}

.page-desc {
  margin-top: 8px;
  color: #64748b;
  font-size: 13px;
}

.scope-alert,
.filter-card,
.section-block {
  margin-bottom: 16px;
}

.filter-card,
.section-block,
.inner-card,
.selected-order-card {
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.main-tabs ::v-deep .el-tabs__item {
  font-weight: 600;
}

.section-block {
  padding: 18px;
  background: rgba(255, 255, 255, 0.9);
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

.metric-grid,
.chart-grid,
.table-grid,
.selected-order-row {
  margin-top: 0;
}

.compact-grid {
  row-gap: 16px;
}

.metric-card,
.mini-card,
.neutral-card,
.summary-card {
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

.summary-card.total {
  background: linear-gradient(135deg, #0f766e, #14b8a6);
}

.summary-card.pending {
  background: linear-gradient(135deg, #b45309, #f59e0b);
}

.summary-card.settled {
  background: linear-gradient(135deg, #166534, #22c55e);
}

.summary-card.flow {
  background: linear-gradient(135deg, #7c3aed, #a78bfa);
}

.metric-label,
.mini-title,
.neutral-title,
.summary-label {
  font-size: 14px;
  opacity: 0.95;
}

.metric-value,
.mini-value,
.neutral-value,
.summary-value {
  margin: 10px 0 6px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
}

.metric-sub,
.summary-extra {
  font-size: 13px;
  opacity: 0.92;
}

.chart-card {
  border-radius: 18px;
  overflow: hidden;
}

.card-header,
.inner-card-header {
  font-weight: 700;
  color: #0f172a;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.chart-box.large {
  height: 360px;
}

.inner-card,
.selected-order-card {
  height: 100%;
}

.selected-order-content {
  display: grid;
  gap: 10px;
  color: #334155;
  font-size: 14px;
}

.item-filter {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.empty-text {
  color: #94a3b8;
  line-height: 1.8;
}

@media (max-width: 992px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .section-head {
    align-items: flex-start;
  }

  .item-filter {
    justify-content: flex-start;
  }

  .chart-box,
  .chart-box.large {
    height: 300px;
  }
}
</style>
