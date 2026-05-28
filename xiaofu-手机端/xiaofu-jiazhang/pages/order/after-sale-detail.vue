<template>
  <view class="container">
    <view class="status-card">
      <view class="status-icon">
        <uni-icons :type="getStatusIcon(afterSale.status)" size="30" color="#fff"></uni-icons>
      </view>
      <view class="status-content">
        <text class="status-title">{{ getStatusText(afterSale.status) }}</text>
        <text class="status-desc">{{ getStatusDesc(afterSale.status) }}</text>
      </view>
    </view>

    <view class="card">
      <view class="card-title">售后信息</view>
      <view class="info-row">
        <text class="label">售后单号</text>
        <text class="value">{{ afterSale.afterSaleNo || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ afterSale.orderNo || order.orderNo || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="label">申请类型</text>
        <text class="value">{{ getApplyTypeText(afterSale.applyType) }}</text>
      </view>
      <view class="info-row">
        <text class="label">申请数量</text>
        <text class="value">{{ afterSale.applyNum || 0 }}</text>
      </view>
      <view class="info-row">
        <text class="label">申请金额</text>
        <text class="value amount">¥{{ formatMoney(afterSale.applyAmount) }}</text>
      </view>
      <view class="info-row" v-if="afterSale.targetSizeName">
        <text class="label">换货尺码</text>
        <text class="value">{{ afterSale.targetSizeName }}</text>
      </view>
      <view class="info-row">
        <text class="label">提交时间</text>
        <text class="value">{{ afterSale.createTime || '-' }}</text>
      </view>
      <view class="reason-box">
        <text class="reason-label">申请原因</text>
        <text class="reason-text">{{ afterSale.reason || '暂无' }}</text>
      </view>
    </view>

    <view class="card" v-if="product.name">
      <view class="card-title">关联商品</view>
      <view class="product-card">
        <image class="product-image" :src="product.image" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ product.name }}</text>
          <text class="product-spec">{{ product.size || '-' }}<text v-if="product.color && product.color !== '-'"> | {{ product.color }}</text></text>
          <text class="product-meta">数量 x{{ product.quantity || 1 }}</text>
        </view>
        <text class="product-price">¥{{ product.price || '0.00' }}</text>
      </view>
    </view>

    <view class="card" v-if="afterSale.auditBy || afterSale.auditTime || afterSale.handleResult || afterSale.handleRemark || afterSale.finishTime">
      <view class="card-title">处理进度</view>
      <view class="info-row" v-if="afterSale.auditBy">
        <text class="label">审核人</text>
        <text class="value">{{ afterSale.auditBy }}</text>
      </view>
      <view class="info-row" v-if="afterSale.auditTime">
        <text class="label">审核时间</text>
        <text class="value">{{ afterSale.auditTime }}</text>
      </view>
      <view class="info-row" v-if="afterSale.handleResult">
        <text class="label">处理结果</text>
        <text class="value">{{ afterSale.handleResult }}</text>
      </view>
      <view class="info-row" v-if="afterSale.handleRemark">
        <text class="label">处理备注</text>
        <text class="value">{{ afterSale.handleRemark }}</text>
      </view>
      <view class="info-row" v-if="afterSale.finishTime">
        <text class="label">完成时间</text>
        <text class="value">{{ afterSale.finishTime }}</text>
      </view>
      <view class="info-row">
        <text class="label">退款状态</text>
        <text class="value">{{ getRefundStatusText(afterSale.refundStatus) }}</text>
      </view>
      <view class="info-row" v-if="afterSale.actualRefundAmount">
        <text class="label">实际退款</text>
        <text class="value amount">¥{{ formatMoney(afterSale.actualRefundAmount) }}</text>
      </view>
    </view>

    <view class="action-bar">
      <button class="action-btn plain" @click="goOrderDetail">查看订单</button>
      <button class="action-btn primary" @click="goOrderList">返回订单列表</button>
    </view>
  </view>
</template>

<script>
import { getOrder } from '@/api/order'
import { getAfterSale, listAfterSale } from '@/api/afterSale'

export default {
  data() {
    return {
      orderId: '',
      afterSaleId: '',
      afterSaleNo: '',
      order: {},
      afterSale: {
        status: '1',
        refundStatus: '0'
      }
    }
  },
  computed: {
    product() {
      const list = this.order.products || this.order.items || this.order.orderItems || []
      if (!list.length) return {}
      if (!this.afterSale.orderItemId) return list[0]
      return list.find((item) => String(item.orderItemId || item.id) === String(this.afterSale.orderItemId)) || list[0]
    }
  },
  onLoad(options) {
    this.orderId = options.id || options.orderId || ''
    this.afterSaleId = options.afterSaleId || ''
    this.afterSaleNo = options.afterSaleNo ? decodeURIComponent(options.afterSaleNo) : ''
    this.loadData()
  },
  methods: {
    async loadData() {
      await Promise.all([this.loadOrder(), this.loadAfterSale()])
    },
    async loadOrder() {
      if (!this.orderId) return
      try {
        const res = await getOrder(this.orderId)
        this.order = res.data || res || {}
      } catch (error) {
        uni.showToast({
          title: error.message || '订单信息加载失败',
          icon: 'none'
        })
      }
    },
    async loadAfterSale() {
      try {
        if (this.afterSaleId) {
          const res = await getAfterSale(this.afterSaleId)
          this.afterSale = res.data || res || this.afterSale
          return
        }
        const res = await listAfterSale({
          orderId: this.orderId,
          pageNum: 1,
          pageSize: 20
        })
        const rows = res.rows || []
        const matched = this.afterSaleNo
          ? rows.find((item) => item.afterSaleNo === this.afterSaleNo)
          : rows[0]
        if (matched) {
          this.afterSale = matched
          this.afterSaleId = matched.id
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '售后信息加载失败',
          icon: 'none'
        })
      }
    },
    getApplyTypeText(type) {
      const map = {
        '1': '仅退款',
        '2': '退货退款',
        '3': '换货'
      }
      return map[String(type || '2')] || '退换申请'
    },
    getStatusText(status) {
      const map = {
        '1': '待处理',
        '3': '已发货',
        '4': '已完成'
      }
      return map[String(status || '1')] || '待处理'
    },
    getStatusDesc(status) {
      const map = {
        '1': '售后申请已通过，请等待供应商发货。',
        '3': '供应商已发货，请留意物流或收货通知。',
        '4': '售后处理已完成。'
      }
      return map[String(status || '1')] || '售后申请已通过，请等待供应商发货。'
    },
    getStatusIcon(status) {
      const map = {
        '1': 'calendar',
        '3': 'paperplane',
        '4': 'checkbox-filled'
      }
      return map[String(status || '1')] || 'help'
    },
    getRefundStatusText(status) {
      const map = {
        '0': '未退款',
        '1': '退款中',
        '2': '退款成功',
        '3': '退款失败'
      }
      return map[String(status || '0')] || '未退款'
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    goOrderDetail() {
      if (!this.orderId) return
      uni.navigateTo({
        url: `/pages/order/order-detail?id=${this.orderId}`
      })
    },
    goOrderList() {
      uni.navigateBack({
        delta: 2
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 24rpx;
  background: #f5f7fb;
  box-sizing: border-box;
}

.status-card {
  display: flex;
  align-items: center;
  padding: 32rpx 28rpx;
  margin-bottom: 24rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #3b7aff 0%, #66b8ff 100%);
  color: #fff;
}

.status-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  background: rgba(255, 255, 255, 0.18);
}

.status-content {
  flex: 1;
}

.status-title {
  display: block;
  font-size: 34rpx;
  font-weight: 600;
}

.status-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.5;
  opacity: 0.92;
}

.card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  background: #fff;
  border-radius: 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(59, 122, 255, 0.05);
}

.card-title {
  margin-bottom: 20rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f1f3f7;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  font-size: 26rpx;
  color: #666;
}

.value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #222;
  word-break: break-all;
}

.amount {
  color: #f04438;
  font-weight: 600;
}

.reason-box {
  margin-top: 16rpx;
  padding: 22rpx;
  border-radius: 16rpx;
  background: #f8faff;
}

.reason-label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  color: #666;
}

.reason-text {
  display: block;
  font-size: 26rpx;
  color: #222;
  line-height: 1.7;
}

.product-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.product-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 16rpx;
  background: #eef4ff;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  display: block;
  font-size: 28rpx;
  color: #222;
  font-weight: 600;
  line-height: 1.5;
}

.product-spec,
.product-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #888;
}

.product-price {
  font-size: 28rpx;
  color: #f04438;
  font-weight: 600;
}

.action-bar {
  display: flex;
  gap: 20rpx;
  margin-top: 12rpx;
  padding-bottom: 20rpx;
}

.action-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 28rpx;

  &::after {
    border: none;
  }
}

.action-btn.plain {
  background: #fff;
  color: #4b5563;
  border: 1rpx solid #d8dee9;
}

.action-btn.primary {
  background: #3b7aff;
  color: #fff;
}
</style>
