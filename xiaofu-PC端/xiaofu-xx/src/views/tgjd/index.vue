<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams" label-width="150px">
      <el-form-item label="付款截止日期：">
        <el-select v-model="queryParams.dateType" placeholder="请选择" style="width: 120px;"
          :disabled="queryParams.enableDateRange">
          <el-option label="全部" value=""></el-option>
          <el-option label="近一周" value="week"></el-option>
          <el-option label="近一月" value="month"></el-option>
          <el-option label="近三月" value="quarter"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-checkbox v-model="queryParams.enableDateRange">统购结束</el-checkbox>
      </el-form-item>
      <el-form-item label="" class="date-range">
        <el-date-picker v-model="queryParams.startTime" type="datetime" placeholder="开始日期" style="width: 200px;"
          value-format="yyyy-MM-dd HH:mm:ss" :disabled="!queryParams.enableDateRange">
        </el-date-picker>
        <el-date-picker v-model="queryParams.endTime" type="datetime" placeholder="结束日期" style="width: 200px;"
          value-format="yyyy-MM-dd HH:mm:ss" :disabled="!queryParams.enableDateRange">
        </el-date-picker>
      </el-form-item>

      <el-form-item label="订购率区间：">
        <el-input v-model="queryParams.orderRateMin" placeholder="最小值" style="width:150px;">
          <template slot="append">%</template>
        </el-input>
        <span style="margin: 0 5px;">—</span>
        <el-input v-model="queryParams.orderRateMax" placeholder="最大值" style="width: 150px;">
          <template slot="append">%</template>
        </el-input>
      </el-form-item>

      <el-form-item label="" class="tip-label">
        <span style="color: #f56c6c;">*订购率=已订购人数/预计销售人数</span>
      </el-form-item>
      <br>
      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.manufacturerName" placeholder="请输入厂商名称" style="width: 200px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="handleQuery">查询</el-button>
        <el-button size="small" @click="handleExport">数据导出</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" style="width: 100%;">
      <el-table-column label="公告名称" prop="noticeName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column label="推广总人数" prop="promotionCount" align="center" width="120"></el-table-column>
      <el-table-column label="预计销售人数" prop="expectedSalesCount" align="center" width="120"></el-table-column>
      <el-table-column label="已订购人数" prop="orderCount" align="center" width="120"></el-table-column>
      <el-table-column label="订购率" prop="orderRate" align="center" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.orderRate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="统购时间区间" prop="timeRange" align="center" width="180"></el-table-column>
      <el-table-column label="剩余时间" prop="remainTime" align="center" width="120"></el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList"></pagination>

    <!-- 查看详情弹窗 -->
    <el-dialog title="订购详情" :visible.sync="dialogVisible" width="900px" append-to-body>
      <div class="detail-container">
        <div class="detail-header">
          <div class="item">
            <span class="label">公告名称：</span>
            <span class="value">{{ detailInfo.noticeName }}</span>
          </div>
          <div class="item">
            <span class="label">厂商名称：</span>
            <span class="value">{{ detailInfo.manufacturerName }}</span>
          </div>
          <div class="item">
            <span class="label">推广总人数：</span>
            <span class="value">{{ detailInfo.promotionCount }}</span>
          </div>
          <div class="item">
            <span class="label">预计销售人数：</span>
            <span class="value">{{ detailInfo.expectedSalesCount }}</span>
          </div>
          <div class="item">
            <span class="label">已订购人数：</span>
            <span class="value highlight">{{ detailInfo.orderCount }}</span>
          </div>
          <div class="item">
            <span class="label">订购率：</span>
            <span class="value highlight">{{ detailInfo.orderRate }}%</span>
          </div>
        </div>

        <div class="form-section-title">订购明细</div>

        <el-table :data="detailInfo.orderList" border style="width: 100%">
          <el-table-column label="商品名称" prop="productName" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column label="商品单价" prop="price" width="120" align="center"></el-table-column>
          <el-table-column label="订购数量" prop="quantity" width="120" align="center"></el-table-column>
          <el-table-column label="订购人" prop="orderPerson" width="120" align="center"></el-table-column>
          <el-table-column label="订购时间" prop="orderTime" width="180" align="center"></el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleExportDetail">导出明细</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TgjdIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 表格数据
      tableData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateType: '',
        enableDateRange: false,
        startTime: '2025-03-18 00:00:00',
        endTime: '2025-04-17 23:59:59',
        orderRateMin: '',
        orderRateMax: '',
        manufacturerName: ''
      },
      // 弹窗显示状态
      dialogVisible: false,
      // 详情数据
      detailInfo: {
        noticeName: '',
        manufacturerName: '',
        promotionCount: 0,
        expectedSalesCount: 0,
        orderCount: 0,
        orderRate: 0,
        orderList: []
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 这里应该调用后端接口获取数据
      setTimeout(() => {
        // 模拟数据
        this.tableData = [
          {
            id: 1,
            noticeName: '2023年春季校服采购通知',
            manufacturerName: '河北金鑫服装制造有限公司',
            promotionCount: 500,
            expectedSalesCount: 450,
            orderCount: 350,
            orderRate: 77.78,
            timeRange: '2023-03-18 ~ 2023-04-17',
            remainTime: '3天'
          },
          {
            id: 2,
            noticeName: '2023年秋季校服采购通知',
            manufacturerName: '山西顺贝达服饰有限公司',
            promotionCount: 600,
            expectedSalesCount: 580,
            orderCount: 480,
            orderRate: 82.76,
            timeRange: '2023-08-10 ~ 2023-09-10',
            remainTime: '已截止'
          }
        ];
        this.total = 2;
        this.loading = false;
      }, 500);
    },

    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 数据导出操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有统购进度数据？').then(() => {
        this.$modal.msgSuccess("导出成功");
      }).catch(() => { });
    },

    /** 查看详情按钮操作 */
    handleView(row) {
      this.dialogVisible = true;
      this.detailInfo = {
        noticeName: row.noticeName,
        manufacturerName: row.manufacturerName,
        promotionCount: row.promotionCount,
        expectedSalesCount: row.expectedSalesCount,
        orderCount: row.orderCount,
        orderRate: row.orderRate,
        orderList: [
          {
            productName: '夏季短袖校服',
            price: '¥128',
            quantity: 2,
            orderPerson: '张三家长',
            orderTime: '2023-03-20 10:30:45'
          },
          {
            productName: '运动长裤',
            price: '¥98',
            quantity: 1,
            orderPerson: '李四家长',
            orderTime: '2023-03-21 14:22:15'
          }
        ]
      };
    },

    /** 导出明细操作 */
    handleExportDetail() {
      this.$modal.msgSuccess("明细导出成功");
      this.dialogVisible = false;
    }
  }
};
</script>

<style scoped>
.search-form {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.search-form .el-form-item {
  margin-bottom: 8px;
  margin-right: 15px;
}

.search-form .el-button {
  margin-left: 5px;
}

.date-range {
  margin-left: 0;
}

.tip-label {
  margin-right: 15px;
}

.detail-container {
  padding: 0 20px;
}

.detail-header {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-gap: 15px;
  margin-bottom: 20px;
}

.detail-header .item {
  display: flex;
  align-items: center;
}

.detail-header .label {
  width: 120px;
  color: #606266;
  font-weight: 500;
}

.detail-header .value {
  flex: 1;
  color: #303133;
}

.detail-header .highlight {
  color: #409EFF;
  font-weight: bold;
}

:deep(.el-input-group__append) {
  padding: 0 10px;
}

:deep(.form-section-title) {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 15px;
  color: #303133;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}
</style>
