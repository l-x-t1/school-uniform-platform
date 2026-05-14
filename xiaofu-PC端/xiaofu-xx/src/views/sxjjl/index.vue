<template>
  <div class="app-container">
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="厂商名称：">
        <el-input v-model="queryParams.manufacturerName" placeholder="请输入厂商名称" clearable style="width: 220px;" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品状态：">
        <el-select v-model="queryParams.productStatus" placeholder="全部" style="width: 220px;">
          <el-option label="全部" value="" />
          <el-option label="上架中" value="1" />
          <el-option label="已下架" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品品类：">
        <el-select v-model="queryParams.productCategory" placeholder="全部" style="width: 220px;">
          <el-option label="全部" value="" />
          <el-option label="校服" value="1" />
          <el-option label="运动服" value="2" />
          <el-option label="书包" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品款式：">
        <el-select v-model="queryParams.productStyle" placeholder="全部" style="width: 220px;">
          <el-option label="全部" value="" />
          <el-option label="春秋款" value="1" />
          <el-option label="夏季款" value="2" />
          <el-option label="冬季款" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="待受理" name="pending" />
      <el-tab-pane label="已通过" name="approved" />
      <el-tab-pane label="已退回" name="returned" />
      <el-tab-pane label="已更换" name="changed" />
    </el-tabs>

    <el-table v-loading="loading" :data="tableData" style="width: 100%">
      <el-table-column label="商品信息" prop="productInfo" min-width="300">
        <template slot-scope="scope">
          <div class="product-info">
            <div v-if="scope.row.imageUrl" class="product-img">
              <img :src="scope.row.imageUrl" alt="商品图片">
            </div>
            <div class="product-details">
              <div class="product-name">{{ scope.row.productName || '-' }}</div>
              <div class="product-code">款号: {{ scope.row.productCode || '-' }}</div>
              <div class="product-category">品类: {{ scope.row.categoryName || '-' }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="厂商名称" prop="manufacturerName" min-width="150" />
      <el-table-column label="价格/时间" prop="price" min-width="150">
        <template slot-scope="scope">
          <div>￥{{ scope.row.price || '-' }}</div>
          <div class="publish-time">{{ parseTime(scope.row.operateTime, '{y}-{m}-{d}') || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="tableData.length === 0 && !loading" class="empty-data">
      <span>无数据记录</span>
    </div>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="尺码调换记录详情" :visible.sync="detailOpen" width="760px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="售后单号">{{ detailInfo.afterSaleNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detailInfo.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ detailInfo.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商品款号">{{ detailInfo.productCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="厂商名称">{{ detailInfo.manufacturerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商品品类">{{ detailInfo.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原尺码">{{ detailInfo.sizeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调换尺码">{{ detailInfo.targetSizeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请数量">{{ detailInfo.applyNum || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detailInfo.status) }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ detailInfo.price || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(detailInfo.createTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ parseTime(detailInfo.operateTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ detailInfo.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ detailInfo.handleResult || detailInfo.handleRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { getSizeExchange, listSizeExchange } from '@/api/tonggou/sxjjl'

export default {
  name: 'SxjjlIndex',
  data() {
    return {
      loading: false,
      total: 0,
      tableData: [],
      detailOpen: false,
      detailInfo: {},
      activeTab: 'pending',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        manufacturerName: '',
        productStatus: '',
        productCategory: '',
        productStyle: '',
        status: '0'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSizeExchange(this.queryParams).then(response => {
        this.tableData = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleTabClick(tab) {
      const statusMap = {
        pending: '0',
        approved: '1',
        returned: '2',
        changed: '4'
      }
      this.queryParams.status = statusMap[tab.name]
      this.handleQuery()
    },
    statusText(status) {
      const textMap = {
        '0': '待受理',
        '1': '已通过',
        '2': '已退回',
        '3': '处理中',
        '4': '已更换',
        '5': '已关闭'
      }
      return textMap[status] || '-'
    },
    statusTagType(status) {
      const typeMap = {
        '0': 'warning',
        '1': 'success',
        '2': 'danger',
        '4': 'success'
      }
      return typeMap[status] || 'info'
    },
    handleView(row) {
      this.detailOpen = true
      this.detailInfo = row
      getSizeExchange(row.id).then(response => {
        this.detailInfo = response.data || row
      })
    }
  }
}
</script>

<style scoped>
.product-info {
  display: flex;
  align-items: center;
}

.product-img {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.product-code,
.product-category {
  font-size: 12px;
  color: #606266;
  margin-bottom: 3px;
}

.publish-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.empty-data {
  text-align: center;
  color: #909399;
  padding: 30px 0;
}
</style>
