<template>
  <view class="container">
    <view class="order-card">
      <view class="card-title">订单评价</view>
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ order.orderNo || '-' }}</text>
      </view>
    </view>

    <view class="comment-card" v-for="(item, index) in commentItems" :key="index">
      <view class="product">
        <image class="product-image" :src="item.image" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ item.name }}</text>
          <text class="product-spec">{{ item.size || '-' }} | x{{ item.quantity || 1 }}</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">评分</text>
        <uni-rate :value="item.score" size="24" @change="onScoreChange(index, $event)"></uni-rate>
      </view>

      <view class="form-item column">
        <text class="form-label">评价内容</text>
        <textarea
          class="textarea"
          v-model="item.content"
          maxlength="300"
          placeholder="请填写商品质量、尺码、穿着感受等评价"
        />
        <text class="count">{{ item.content.length }}/300</text>
      </view>
    </view>

    <button class="submit-btn" :disabled="submitting" @click="submitComment">
      {{ submitting ? '提交中...' : '提交评价' }}
    </button>
  </view>
</template>

<script>
import config from '@/config'
import { getOrder } from '@/api/order'
import { createComment } from '@/api/comment'

export default {
  data() {
    return {
      orderId: '',
      order: {},
      commentItems: [],
      submitting: false
    }
  },
  onLoad(options) {
    this.orderId = options.id || options.orderId || ''
    const cachedOrder = uni.getStorageSync('pendingCommentOrder') || {}
    if (String(cachedOrder.id || cachedOrder.orderId || '') === String(this.orderId)) {
      this.initOrder(cachedOrder)
    } else {
      this.order = {
        id: this.orderId,
        orderNo: options.orderNo ? decodeURIComponent(options.orderNo) : ''
      }
    }
    if (this.orderId) {
      this.loadOrder()
    }
  },
  methods: {
    loadOrder() {
      getOrder(this.orderId)
        .then((res) => {
          this.initOrder(res.data || res || {})
        })
        .catch((error) => {
          const localOrders = uni.getStorageSync('devOrders') || {}
          if (config.enableMockFallback && localOrders[this.orderId]) {
            this.initOrder(localOrders[this.orderId])
            return
          }
          uni.showToast({
            title: error.message || '订单加载失败',
            icon: 'none'
          })
        })
    },

    initOrder(order) {
      const products = order.products || order.items || order.orderItems || order.xfptOrderItemList || []
      this.order = order
      this.commentItems = products.map((item) => ({
        ...item,
        name: item.name || item.goodsName || '校服',
        image: item.image || item.mainImage || '/static/qjxf.png',
        quantity: item.quantity || item.buyNum || 1,
        size: item.size || item.sizeName || '-',
        orderItemId: item.orderItemId || item.id,
        score: 5,
        content: ''
      }))
    },

    onScoreChange(index, event) {
      this.commentItems[index].score = Number(event.value || 5)
    },

    validateForm() {
      if (!this.orderId) return '缺少订单信息'
      if (!this.commentItems.length) return '没有可评价的商品'
      const emptyItem = this.commentItems.find((item) => !item.content.trim())
      if (emptyItem) return '请填写评价内容'
      return ''
    },

    buildPayload(item) {
      return {
        orderId: Number(this.order.id || this.orderId),
        orderItemId: item.orderItemId || item.id,
        orderNo: this.order.orderNo || '',
        goodsId: item.goodsId || item.uniformId || item.id,
        goodsName: item.name || item.goodsName || '',
        supplierId: item.supplierId || this.order.supplierId,
        score: item.score,
        content: item.content.trim()
      }
    },

    submitComment() {
      const message = this.validateForm()
      if (message) {
        uni.showToast({ title: message, icon: 'none' })
        return
      }

      this.submitting = true
      uni.showLoading({ title: '提交中...' })
      Promise.all(this.commentItems.map((item) => createComment(this.buildPayload(item))))
        .then(() => {
          this.markLocalOrderCommented()
          uni.hideLoading()
          uni.showToast({ title: '评价已提交', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 800)
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '提交失败',
            icon: 'none'
          })
        })
        .finally(() => {
          this.submitting = false
        })
    },

    markLocalOrderCommented() {
      const localOrders = uni.getStorageSync('devOrders') || {}
      if (localOrders[this.orderId]) {
        localOrders[this.orderId].commented = true
        uni.setStorageSync('devOrders', localOrders)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 130rpx;
  background: #f5f7fb;
  box-sizing: border-box;
}

.order-card,
.comment-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 20rpx;
}

.info-row,
.form-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 76rpx;
  border-bottom: 1rpx solid #f1f1f1;
}

.info-row:last-child,
.form-item:last-child {
  border-bottom: none;
}

.label,
.form-label {
  font-size: 28rpx;
  color: #555;
}

.value {
  max-width: 470rpx;
  font-size: 28rpx;
  color: #222;
  text-align: right;
  word-break: break-all;
}

.product {
  display: flex;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #f1f1f1;
}

.product-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 8rpx;
  background: #f2f2f2;
  margin-right: 20rpx;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  display: block;
  font-size: 28rpx;
  color: #222;
  line-height: 1.4;
}

.product-spec {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #888;
}

.column {
  align-items: flex-start;
  flex-direction: column;
  padding: 22rpx 0;
}

.textarea {
  width: 100%;
  height: 180rpx;
  margin-top: 18rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background: #f8f9fb;
  border-radius: 8rpx;
  font-size: 28rpx;
  color: #222;
}

.count {
  align-self: flex-end;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #999;
}

.submit-btn {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 24rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  background: #3B7AFF;
  color: #fff;
  font-size: 30rpx;
}

.submit-btn[disabled] {
  background: #a9c4ff;
  color: #fff;
}
</style>
