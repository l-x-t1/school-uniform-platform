<template>
  <div class="app-container">
    <!-- -->
    <el-form :inline="true" :model="wholesaleQuery">
      <el-form-item label="日期：">
        <el-date-picker v-model="wholesaleQuery.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="yyyy-MM-dd" style="width: 240px"></el-date-picker>
      </el-form-item>

      <el-form-item label="厂商名称：">
        <el-input v-model="wholesaleQuery.manufacturerName" placeholder="请输入厂商名称" style="width: 240px;"></el-input>
      </el-form-item>
      <el-form-item label="结算模式：">
        <el-select v-model="wholesaleQuery.settlementMode" placeholder="全部" style="width: 240px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="按订单结算" value="1"></el-option>
          <el-option label="按月结算" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态：">
        <el-select v-model="wholesaleQuery.orderStatus" placeholder="全部" style="width: 240px;">
          <el-option label="全部" value=""></el-option>
          <el-option label="已结算" value="1"></el-option>
          <el-option label="未结算" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleWholesaleQuery">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 统购数据表格 -->
    <el-table v-loading="wholesaleLoading" :data="wholesaleTableData">
      <el-table-column label="申请时间" prop="noticeName" min-width="180"></el-table-column>
      <el-table-column label="地区" prop="noticeTime" width="160"></el-table-column>
      <el-table-column label="学校名称" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="交易总金额（元）" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="商品总金额（元）" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="运费总金额(元)	" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="业务类别" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="配送方式" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="订单数" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="订购数量(套/件)" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="结算模式" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="处理状态" prop="manufacturerName" min-width="150"></el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <!-- <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="text" @click="handlePay(scope.row)"
            v-if="scope.row.orderStatus === '1'">付款</el-button> -->
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'RuoyiXxIndex',

  data() {
    return {
      wholesaleQuery: {
        dateRange: [],
        noticeName: '',
        manufacturerName: '',
        settlementMode: '',
        orderStatus: ''
      }
    };
  },

  mounted() {

  },

  methods: {

  },
};
</script>