<template>
  <div class="app-container">
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <el-tab-pane label="统购" name="wholesale">
        <!-- 统购查询表单 -->
        <el-form :inline="true" :model="wholesaleQuery">
          <el-form-item label="公告发布时间：">
            <el-date-picker v-model="wholesaleQuery.dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd"
              style="width: 240px"></el-date-picker>
          </el-form-item>
          <el-form-item label="公告名称：">
            <el-input v-model="wholesaleQuery.noticeName" placeholder="请输入公告名称" style="width: 240px;"></el-input>
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
          <el-form-item label="订购状态：">
            <el-select v-model="wholesaleQuery.orderStatus" placeholder="全部" style="width: 240px;">
              <el-option label="全部" value=""></el-option>
              <el-option label="待付款" value="1"></el-option>
              <el-option label="已付款" value="2"></el-option>
              <el-option label="已取消" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" @click="handleWholesaleQuery">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" @click="handleExport">列表导出</el-button>
          </el-form-item>
        </el-form>

        <!-- 统购数据表格 -->
        <el-table v-loading="wholesaleLoading" :data="wholesaleTableData">
          <el-table-column label="公告名称" prop="noticeName" min-width="180"></el-table-column>
          <el-table-column label="公告发布时间" prop="noticeTime" width="160"></el-table-column>
          <el-table-column label="厂商名称" prop="manufacturerName" min-width="150"></el-table-column>
          <el-table-column label="结算模式" prop="settlementMode" width="100">
            <template slot-scope="scope">
              {{ scope.row.settlementMode === '1' ? '按订单结算' : scope.row.settlementMode === '2' ? '按月结算' : '' }}
            </template>
          </el-table-column>
          <el-table-column label="订购状态" prop="orderStatus" width="100">
            <template slot-scope="scope">
              <el-tag
                :type="scope.row.orderStatus === '1' ? 'warning' : scope.row.orderStatus === '2' ? 'success' : 'info'">
                {{ scope.row.orderStatus === '1' ? '待付款' : scope.row.orderStatus === '2' ? '已付款' : '已取消' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="交易总金额(元)" prop="totalAmount" width="130"></el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
              <el-button size="mini" type="text" @click="handlePay(scope.row)"
                v-if="scope.row.orderStatus === '1'">付款</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 无数据提示 -->
        <div v-if="wholesaleTableData.length === 0 && !wholesaleLoading" class="empty-block">
          <span>暂无数据</span>
        </div>

        <!-- 分页组件 -->
        <pagination v-show="wholesaleTotal > 0" :total="wholesaleTotal" :page.sync="wholesaleQuery.pageNum"
          :limit.sync="wholesaleQuery.pageSize" @pagination="getWholesaleList" />
      </el-tab-pane>

      <el-tab-pane label="零售" name="retail">
        <!-- 零售查询表单 -->
        <el-form :inline="true" :model="retailQuery">
          <el-form-item label="订单提交时间：">
            <el-date-picker v-model="retailQuery.dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd"
              style="width: 240px"></el-date-picker>
          </el-form-item>
          <el-form-item label="结算模式：">
            <el-select v-model="retailQuery.evaluationStatus" placeholder="全部" style="width: 240px;">
              <el-option label="全部" value=""></el-option>
              <el-option label="钱到商家交易模式" value="1"></el-option>
              <el-option label="钱统一到学校账号" value="2"></el-option>
              <el-option label="钱统一到学校账号(拉卡拉收款)" value="2"></el-option>
              <el-option label="钱统一到区县教育局账号" value="2"></el-option>
              <el-option label="钱统一到市教育局账号" value="2"></el-option>
              <el-option label="钱统一到省厅教育局账号" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="订单号：">
            <el-input v-model="retailQuery.orderNo" placeholder="请输入订单号" style="width: 240px;"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" @click="handleRetailQuery">查询</el-button>
          </el-form-item>
        </el-form>
        <el-button size="small" type="primary" @click="handleExport">列表导出</el-button>
        <!-- 零售数据表格 -->
        <el-table style="margin-top: 10px;" v-loading="retailLoading" :data="retailTableData">
          <!-- 表格内容将根据零售查询结果生成 -->
          <el-table-column label="厂商名称">

          </el-table-column>
          <el-table-column label="交易总金额（元）">

          </el-table-column>
          <el-table-column label="操作">

          </el-table-column>
        </el-table>



        <!-- 分页组件 -->
        <pagination v-show="retailTotal > 0" :total="retailTotal" :page.sync="retailQuery.pageNum"
          :limit.sync="retailQuery.pageSize" @pagination="getRetailList" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'JymxIndex',
  data() {
    return {
      // 当前激活的标签页
      activeTab: 'wholesale',

      // 统购相关数据
      wholesaleLoading: false,
      wholesaleTotal: 0,
      wholesaleTableData: [],
      wholesaleQuery: {
        pageNum: 1,
        pageSize: 10,
        dateRange: ['2025-03-17', '2025-04-16'],
        noticeName: '',
        manufacturerName: '',
        settlementMode: '',
        orderStatus: ''
      },

      // 零售相关数据
      retailLoading: false,
      retailTotal: 0,
      retailTableData: [],
      retailQuery: {
        pageNum: 1,
        pageSize: 10,
        evaluationStatus: '',
        orderNo: ''
      }
    };
  },
  created() {
    this.getWholesaleList();
  },
  methods: {
    // 标签页切换
    handleTabClick(tab) {
      if (tab.name === 'wholesale') {
        this.getWholesaleList();
      } else if (tab.name === 'retail') {
        this.getRetailList();
      }
    },

    /** 统购列表查询 */
    getWholesaleList() {
      this.wholesaleLoading = true;
      // 模拟接口调用
      setTimeout(() => {
        this.wholesaleTableData = [];
        this.wholesaleTotal = 0;
        this.wholesaleLoading = false;
      }, 300);
    },

    /** 零售列表查询 */
    getRetailList() {
      this.retailLoading = true;
      // 模拟接口调用
      setTimeout(() => {
        this.retailTableData = [];
        this.retailTotal = 0;
        this.retailLoading = false;
      }, 300);
    },

    /** 统购搜索按钮操作 */
    handleWholesaleQuery() {
      this.wholesaleQuery.pageNum = 1;
      this.getWholesaleList();
    },

    /** 零售搜索按钮操作 */
    handleRetailQuery() {
      this.retailQuery.pageNum = 1;
      this.getRetailList();
    },

    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有交易明细数据?').then(() => {
        this.$modal.msgSuccess("导出成功");
      }).catch(() => { });
    },

    /** 查看详情 */
    handleView(row) {
      this.$router.push({ path: '/jymx/detail', query: { id: row.id } });
    },

    /** 付款操作 */
    handlePay(row) {
      this.$router.push({ path: '/jymx/payment', query: { id: row.id } });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-form {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.empty-block {
  text-align: center;
  padding: 30px 0;
  color: #909399;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

:deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
  min-width: 80px;
  text-align: center;
}
</style>
