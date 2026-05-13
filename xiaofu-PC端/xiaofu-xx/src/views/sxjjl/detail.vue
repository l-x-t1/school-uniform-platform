<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button icon="el-icon-back" size="small" @click="goBack">返回</el-button>
    </div>

    <el-card v-loading="loading" shadow="never">
      <div slot="header" class="card-title">尺码调换详情</div>
      <div class="product-block">
        <div v-if="detail.imageUrl" class="product-img">
          <img :src="detail.imageUrl" alt="商品图片">
        </div>
        <div class="product-main">
          <div class="product-name">{{ detail.productName || '-' }}</div>
          <div class="product-sub">款号：{{ detail.productCode || '-' }}</div>
          <div class="product-sub">品类：{{ detail.categoryName || '-' }}</div>
        </div>
        <el-tag :type="statusTagType(detail.status)">{{ statusText(detail.status) }}</el-tag>
      </div>

      <el-row :gutter="24" class="detail-grid">
        <el-col :span="8"><span class="label">厂商名称：</span>{{ detail.manufacturerName || '-' }}</el-col>
        <el-col :span="8"><span class="label">订单编号：</span>{{ detail.orderNo || '-' }}</el-col>
        <el-col :span="8"><span class="label">售后编号：</span>{{ detail.afterSaleNo || '-' }}</el-col>
        <el-col :span="8"><span class="label">价格：</span>{{ detail.price || '-' }}</el-col>
        <el-col :span="8"><span class="label">原尺码：</span>{{ detail.sizeName || '-' }}</el-col>
        <el-col :span="8"><span class="label">调换尺码：</span>{{ detail.targetSizeName || '-' }}</el-col>
        <el-col :span="8"><span class="label">申请数量：</span>{{ detail.applyNum || '-' }}</el-col>
        <el-col :span="8"><span class="label">处理时间：</span>{{ detail.operateTime || '-' }}</el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { getSizeExchange } from '@/api/tonggou/sxjjl'

export default {
  name: 'SxjjlDetail',
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      const id = this.$route.query.id
      if (!id) {
        this.$message.error('缺少尺码调换记录ID')
        return
      }
      this.loading = true
      getSizeExchange(id).then(response => {
        this.detail = response.data || {}
      }).finally(() => {
        this.loading = false
      })
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
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}

.card-title {
  font-weight: 600;
  color: #303133;
}

.product-block {
  display: flex;
  align-items: center;
  padding-bottom: 16px;
  margin-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.product-img {
  width: 72px;
  height: 72px;
  margin-right: 14px;
  border: 1px solid #ebeef5;
  overflow: hidden;
}

.product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-main {
  flex: 1;
}

.product-name {
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.product-sub {
  line-height: 22px;
  color: #606266;
}

.detail-grid .el-col {
  line-height: 34px;
  color: #303133;
}

.label {
  color: #606266;
}
</style>
