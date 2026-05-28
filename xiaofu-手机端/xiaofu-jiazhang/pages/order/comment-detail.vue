<template>
  <view class="container">
    <view class="summary-card">
      <view class="summary-top">
        <view>
          <text class="title">评价详情</text>
          <text class="subtitle" v-if="commentList.length">共{{ commentList.length }}件商品评价</text>
        </view>
        <view class="status-badge">已评价</view>
      </view>
      <view class="summary-row">
        <text class="summary-label">订单编号</text>
        <text class="summary-value">{{ orderNo || '-' }}</text>
      </view>
    </view>

    <view class="comment-card" v-for="(comment, index) in commentList" :key="comment.orderItemId || comment.goodsId || index">
      <view class="product-row">
        <image class="product-image" :src="comment.image || '/static/qjxf.png'" mode="aspectFill"></image>
        <view class="product-info">
          <text class="product-name">{{ comment.goodsName || '校服商品' }}</text>
          <text class="product-spec">{{ comment.size || '-' }} | x{{ comment.quantity || 1 }}</text>
          <text class="comment-time">{{ comment.createTime || '刚刚评价' }}</text>
        </view>
      </view>

      <view class="score-row">
        <text class="section-label">评分</text>
        <view class="stars">
          <text
            v-for="star in 5"
            :key="star"
            class="star"
            :class="{ active: star <= Number(comment.score || 0) }"
          >★</text>
          <text class="score-text">{{ Number(comment.score || 0) }} 分</text>
        </view>
      </view>

      <view class="content-block">
        <text class="section-label">评价内容</text>
        <text class="content-text">{{ comment.content || '暂无评价内容' }}</text>
      </view>

      <view class="reply-block" v-if="comment.replyContent">
        <view class="reply-head">
          <text class="reply-tag">商家回复</text>
          <text class="reply-time" v-if="comment.replyTime">{{ comment.replyTime }}</text>
        </view>
        <text class="reply-text">{{ comment.replyContent }}</text>
      </view>
    </view>

    <view class="empty-card" v-if="!commentList.length && !loading">
      <text class="empty-title">暂无评价详情</text>
      <text class="empty-text">评价数据可能还在同步中，请稍后再试</text>
    </view>
  </view>
</template>

<script>
import { listComments } from '@/api/comment'
import { getOrder } from '@/api/order'

export default {
  data() {
    return {
      orderId: '',
      orderNo: '',
      loading: false,
      commentList: [],
      productMap: {}
    }
  },
  onLoad(options) {
    this.orderId = options.id || ''
    this.orderNo = options.orderNo ? decodeURIComponent(options.orderNo) : ''
    this.hydrateLocalSnapshot()
    this.loadComment()
  },
  methods: {
    hydrateLocalSnapshot() {
      const commentDetails = uni.getStorageSync('commentedOrderDetails') || {}
      const cachedDetail = commentDetails[this.orderId]
      if (cachedDetail && Array.isArray(cachedDetail.items) && cachedDetail.items.length) {
        this.orderNo = this.orderNo || cachedDetail.orderNo || ''
        this.commentList = cachedDetail.items.map((item) => this.normalizeComment(item))
        this.productMap = this.buildProductMap(this.commentList)
        return
      }

      const pendingOrder = uni.getStorageSync('pendingCommentOrder') || {}
      const pendingOrderId = String(pendingOrder.id || pendingOrder.orderId || '')
      if (pendingOrderId === String(this.orderId)) {
        const products = pendingOrder.products || pendingOrder.items || pendingOrder.orderItems || pendingOrder.xfptOrderItemList || []
        this.orderNo = this.orderNo || pendingOrder.orderNo || ''
        this.commentList = products.map((item) => this.normalizeComment(item))
        this.productMap = this.buildProductMap(this.commentList)
      }
    },

    buildProductMap(items = []) {
      return items.reduce((map, item) => {
        const keys = [
          item.orderItemId ? `orderItem:${item.orderItemId}` : '',
          item.goodsId ? `goods:${item.goodsId}` : ''
        ].filter(Boolean)
        keys.forEach((key) => {
          map[key] = {
            image: item.image || '/static/qjxf.png',
            size: item.size || '-',
            quantity: item.quantity || 1,
            goodsName: item.goodsName || '校服商品'
          }
        })
        return map
      }, {})
    },

    async loadOrderProducts() {
      if (!this.orderId) return
      try {
        const res = await getOrder(this.orderId)
        const order = res.data || res || {}
        this.orderNo = this.orderNo || order.orderNo || ''
        const products = order.products || order.items || order.orderItems || order.xfptOrderItemList || []
        if (products.length) {
          this.productMap = this.buildProductMap(products.map((item) => this.normalizeComment(item)))
        }
      } catch (error) {
        // Ignore silently and keep local snapshot data.
      }
    },

    normalizeComment(item = {}) {
      const productInfo =
        this.productMap[`orderItem:${item.orderItemId || item.id}`] ||
        this.productMap[`goods:${item.goodsId}`] ||
        {}
      return {
        id: item.id,
        orderItemId: item.orderItemId || item.id,
        goodsId: item.goodsId || item.uniformId || item.productId || item.id,
        goodsName: item.goodsName || item.name || item.productName || '校服商品',
        image: item.image || item.mainImage || item.productImage || productInfo.image || '/static/qjxf.png',
        size: item.size || item.sizeName || productInfo.size || '-',
        quantity: item.quantity || item.buyNum || productInfo.quantity || 1,
        score: Number(item.score || 0),
        content: item.content || '',
        replyContent: item.replyContent || '',
        replyTime: item.replyTime || '',
        createTime: item.createTime || ''
      }
    },

    loadComment() {
      this.loading = true
      this.loadOrderProducts()
        .finally(() => {
          listComments({ orderId: this.orderId, pageNum: 1, pageSize: 20 })
            .then((res) => {
              const rows = res.rows || []
              if (!rows.length) return
              this.commentList = rows.map((item) => this.normalizeComment(item))
            })
            .catch(() => {
              // Keep local snapshot as fallback.
            })
            .finally(() => {
              this.loading = false
            })
        })
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f3f7ff 0%, #f7f9fc 100%);
  padding: 24rpx;
  box-sizing: border-box;
}

.summary-card,
.comment-card,
.empty-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 12rpx 36rpx rgba(59, 122, 255, 0.08);
  margin-bottom: 24rpx;
}

.summary-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #1f2a44;
}

.subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #8a94a6;
}

.status-badge {
  padding: 10rpx 20rpx;
  border-radius: 999rpx;
  background: rgba(59, 122, 255, 0.1);
  color: #3b7aff;
  font-size: 24rpx;
  font-weight: 500;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #edf2fb;
  gap: 20rpx;
}

.summary-label,
.section-label {
  font-size: 26rpx;
  color: #7e8797;
}

.summary-value {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: #1f2a44;
  word-break: break-all;
}

.product-row {
  display: flex;
  gap: 20rpx;
  align-items: center;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #edf2fb;
}

.product-image {
  width: 136rpx;
  height: 136rpx;
  border-radius: 18rpx;
  background: #eef3ff;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  display: block;
  font-size: 30rpx;
  line-height: 1.45;
  color: #1f2a44;
  font-weight: 600;
}

.product-spec,
.comment-time,
.reply-time {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #8a94a6;
}

.score-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0 18rpx;
}

.stars {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.star {
  font-size: 34rpx;
  color: #d7deeb;
}

.star.active {
  color: #ffb638;
}

.score-text {
  margin-left: 12rpx;
  font-size: 26rpx;
  color: #1f2a44;
  font-weight: 500;
}

.content-block,
.reply-block {
  padding-top: 12rpx;
}

.content-text,
.reply-text {
  display: block;
  margin-top: 16rpx;
  padding: 22rpx 24rpx;
  border-radius: 18rpx;
  background: #f7f9fc;
  font-size: 28rpx;
  line-height: 1.7;
  color: #2b3445;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply-block {
  margin-top: 18rpx;
}

.reply-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20rpx;
}

.reply-tag {
  font-size: 24rpx;
  color: #3b7aff;
  background: rgba(59, 122, 255, 0.1);
  border-radius: 999rpx;
  padding: 8rpx 16rpx;
}

.reply-text {
  background: #eef4ff;
}

.empty-card {
  text-align: center;
  padding: 80rpx 32rpx;
}

.empty-title {
  display: block;
  font-size: 30rpx;
  color: #1f2a44;
  font-weight: 600;
}

.empty-text {
  display: block;
  margin-top: 14rpx;
  font-size: 24rpx;
  color: #8a94a6;
}
</style>
