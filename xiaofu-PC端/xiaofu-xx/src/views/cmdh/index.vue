<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="订单编号：">
        <el-input v-model="queryParams.orderCode" placeholder="请输入订单编号" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item label="溯源编号：">
        <el-input v-model="queryParams.traceCode" placeholder="请输入溯源编号" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.manufacturerName" placeholder="请输入厂商名称" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item label="公告名称：">
        <el-input v-model="queryParams.noticeName" placeholder="请输入公告名称" style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item label="申请调换日期：">
        <el-input v-model="queryParams.applyDateRange" placeholder="2025-04-10 - 2025-04-17"
          style="width: 220px;"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 标签页 -->

    <div v-if="activeTab == 'tab2'" style="position: fixed;top: 160px;right: 20px;" class="right-button">
      <el-button type="primary" size="small" @click="handleExport">报表导出</el-button>
    </div>
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待厂商处理" name="tab1">
        <div class="tab-container">

          <el-table v-loading="loading" :data="tableData" style="width: 100%; margin-top: 10px;">
            <el-table-column label="商品" prop="productName" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column label="原尺码" prop="originalSize" align="center" width="100"></el-table-column>
            <el-table-column label="更换尺码" prop="newSize" align="center" width="100"></el-table-column>
            <el-table-column label="数量" prop="quantity" align="center" width="80"></el-table-column>
            <el-table-column label="收货人信息" prop="receiverInfo" :show-overflow-tooltip="true"
              width="180"></el-table-column>
            <el-table-column label="更换理由" prop="reason" :show-overflow-tooltip="true" width="180"></el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template slot-scope="scope">
                <!-- <el-button size="mini" type="text" @click="handleEdit(scope.row)">详情</el-button>
                <el-button size="mini" type="text" @click="handleApprove(scope.row)"
                  v-if="activeStatus === 'pending'">审核</el-button> -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="厂商已通过" name="tab2">
        <div class="tab-container">



          <el-table v-loading="loading" :data="tableData" style="width: 100%; margin-top: 10px;">
            <el-table-column label="商品" prop="productName" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column label="原尺码" prop="originalSize" align="center" width="100"></el-table-column>
            <el-table-column label="更换尺码" prop="newSize" align="center" width="100"></el-table-column>
            <el-table-column label="数量" prop="quantity" align="center" width="80"></el-table-column>
            <el-table-column label="收货人信息" prop="receiverInfo" :show-overflow-tooltip="true"
              width="180"></el-table-column>
            <el-table-column label="更换理由" prop="reason" :show-overflow-tooltip="true" width="180"></el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template slot-scope="scope">
                <!-- <el-button size="mini" type="text" @click="handleEdit(scope.row)">详情</el-button> -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="厂商已退回" name="tab3">
        <div class="tab-container">
          <el-table v-loading="loading" :data="tableData" style="width: 100%; margin-top: 10px;">
            <el-table-column label="商品" prop="productName" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column label="原尺码" prop="originalSize" align="center" width="100"></el-table-column>
            <el-table-column label="更换尺码" prop="newSize" align="center" width="100"></el-table-column>
            <el-table-column label="数量" prop="quantity" align="center" width="80"></el-table-column>
            <el-table-column label="收货人信息" prop="receiverInfo" :show-overflow-tooltip="true"
              width="180"></el-table-column>
            <el-table-column label="更换理由" prop="reason" :show-overflow-tooltip="true" width="180"></el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template slot-scope="scope">
                <!-- <el-button size="mini" type="text" @click="handleEdit(scope.row)">详情</el-button> -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="厂商已接验" name="tab4">
        <div class="tab-container">
          <el-table v-loading="loading" :data="tableData" style="width: 100%; margin-top: 10px;">
            <el-table-column label="商品" prop="productName" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column label="原尺码" prop="originalSize" align="center" width="100"></el-table-column>
            <el-table-column label="更换尺码" prop="newSize" align="center" width="100"></el-table-column>
            <el-table-column label="数量" prop="quantity" align="center" width="80"></el-table-column>
            <el-table-column label="收货人信息" prop="receiverInfo" :show-overflow-tooltip="true"
              width="180"></el-table-column>
            <el-table-column label="更换理由" prop="reason" :show-overflow-tooltip="true" width="180"></el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template slot-scope="scope">
                <!-- <el-button size="mini" type="text" @click="handleEdit(scope.row)">详情</el-button> -->
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'CmdhIndex',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 当前激活的标签页
      activeTab: 'tab1',
      // 当前激活的状态
      activeStatus: 'pending',
      // 表格数据
      tableData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderCode: '',
        traceCode: '',
        manufacturerName: '',
        noticeName: '',
        applyDateRange: '2025-04-10 - 2025-04-17',
        status: 'pending'
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
      // 模拟接口调用
      setTimeout(() => {
        // 根据不同的状态生成不同的模拟数据
        if (this.activeStatus === 'pending') {
          this.tableData = [
            {
              id: '1',
              productName: '夏季校服短袖T恤',
              originalSize: 'L',
              newSize: 'XL',
              quantity: 1,
              receiverInfo: '张三，13800138000',
              reason: '尺码偏小'
            },
            {
              id: '2',
              productName: '春秋季运动套装',
              originalSize: 'M',
              newSize: 'L',
              quantity: 1,
              receiverInfo: '李四，13900139000',
              reason: '尺码不合适，需要大一号'
            }
          ];
          this.total = 2;
        } else if (this.activeStatus === 'accepted') {
          this.tableData = [
            {
              id: '3',
              productName: '冬季校服外套',
              originalSize: 'XL',
              newSize: 'L',
              quantity: 1,
              receiverInfo: '王五，13700137000',
              reason: '尺码偏大'
            }
          ];
          this.total = 1;
        } else if (this.activeStatus === 'rejected') {
          this.tableData = [
            {
              id: '4',
              productName: '运动鞋',
              originalSize: '42',
              newSize: '43',
              quantity: 1,
              receiverInfo: '赵六，13600136000',
              reason: '尺码偏小'
            }
          ];
          this.total = 1;
        } else if (this.activeStatus === 'received') {
          this.tableData = [
            {
              id: '5',
              productName: '校服长裤',
              originalSize: '30',
              newSize: '32',
              quantity: 1,
              receiverInfo: '钱七，13500135000',
              reason: '收到后发现尺码小了'
            }
          ];
          this.total = 1;
        }
        this.loading = false;
      }, 300);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 标签页点击事件 */
    handleTabClick(tab) {
      // 根据标签页设置状态
      switch (tab.name) {
        case 'tab1':
          this.switchStatus('pending');
          break;
        case 'tab2':
          this.switchStatus('accepted');
          break;
        case 'tab3':
          this.switchStatus('rejected');
          break;
        case 'tab4':
          this.switchStatus('received');
          break;
      }
    },
    /** 切换状态 */
    switchStatus(status) {
      this.activeStatus = status;
      this.queryParams.status = status;

      // 切换标签页
      switch (status) {
        case 'pending':
          this.activeTab = 'tab1';
          break;
        case 'accepted':
          this.activeTab = 'tab2';
          break;
        case 'rejected':
          this.activeTab = 'tab3';
          break;
        case 'received':
          this.activeTab = 'tab4';
          break;
      }

      this.getList();
    },
    /** 详情按钮操作 */
    handleEdit(row) {
      this.$router.push({ path: '/cmdh/detail', query: { id: row.id } });
    },
    /** 审核按钮操作 */
    handleApprove(row) {
      this.$router.push({ path: '/cmdh/approve', query: { id: row.id } });
    },
    /** 导出报表 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有尺码调换数据？').then(() => {
        this.$modal.msgSuccess("导出成功");
      }).catch(() => { });
    }
  }
};
</script>

<style scoped>
.tab-container {
  position: relative;
  padding-top: 10px;
}

.tab-buttons {
  margin-bottom: 15px;
}

.tab-buttons .el-button {
  margin-right: 10px;
}

.active-btn {
  background-color: #ecf5ff !important;
  color: #409EFF !important;
}

.right-button {
  position: absolute;
  right: 0;
  top: 10px;
}

:deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>