<template>
  <view class="container">
    <view class="section">
      <view class="section-title">我的评价</view>
      <view v-if="myComments.length" class="comment-list">
        <view v-for="item in myComments" :key="`mine-${item.id}`" class="comment-card mine">
          <view class="comment-head">
            <text class="comment-user">{{ item.userName || '我' }}</text>
            <text class="comment-score">{{ formatScore(item.score) }}</text>
          </view>
          <text class="comment-time">{{ item.createTime || '' }}</text>
          <text class="comment-content">{{ item.content || '暂无评价内容' }}</text>
        </view>
      </view>
      <view v-else class="empty-text">您还没有评价过这个商品</view>
    </view>

    <view class="section">
      <view class="section-title">全部评价</view>
      <view v-if="allComments.length" class="comment-list">
        <view v-for="item in allComments" :key="`all-${item.id}`" class="comment-card">
          <view class="comment-head">
            <text class="comment-user">{{ item.userName || '匿名用户' }}</text>
            <text class="comment-score">{{ formatScore(item.score) }}</text>
          </view>
          <text class="comment-time">{{ item.createTime || '' }}</text>
          <text class="comment-content">{{ item.content || '暂无评价内容' }}</text>
        </view>
      </view>
      <view v-else class="empty-text">这个商品还没有评价</view>
    </view>
  </view>
</template>

<script>
import { listAllComments } from '@/api/comment'
import { getUserInfo } from '@/utils/auth'

export default {
  data() {
    return {
      orderId: '',
      goodsId: '',
      orderItemId: '',
      myComments: [],
      allComments: []
    }
  },
  onLoad(options) {
    this.orderId = options.orderId || ''
    this.goodsId = options.goodsId || ''
    this.orderItemId = options.orderItemId || ''
    this.loadComments()
  },
  methods: {
    getCurrentUserId() {
      const user = getUserInfo() || {}
      return String(user.userId || user.id || '')
    },

    normalizeRows(res) {
      return res.rows || res.data || res.list || []
    },

    matchCurrentItem(item = {}) {
      if (this.orderItemId && String(item.orderItemId) === String(this.orderItemId)) {
        return true
      }
      if (this.goodsId && String(item.goodsId) === String(this.goodsId)) {
        return true
      }
      return this.orderId ? String(item.orderId) === String(this.orderId) : false
    },

    loadComments() {
      uni.showLoading({ title: '加载中...' })
      listAllComments({
        pageNum: 1,
        pageSize: 500
      })
        .then((res) => {
          const currentUserId = this.getCurrentUserId()
          const rows = this.normalizeRows(res).filter((item) => this.matchCurrentItem(item))
          this.allComments = rows
          this.myComments = rows.filter((item) => String(item.userId) === currentUserId)
          uni.hideLoading()
        })
        .catch((error) => {
          uni.hideLoading()
          uni.showToast({
            title: error.message || '评价加载失败',
            icon: 'none'
          })
        })
    },

    formatScore(score) {
      const numericScore = Number(score || 0)
      return `${numericScore}分`
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 24rpx;
  background: #f5f6f8;
  box-sizing: border-box;
}

.section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 20rpx;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.comment-card {
  padding: 20rpx;
  background: #f8f9fb;
  border-radius: 12rpx;
}

.comment-card.mine {
  border: 2rpx solid #3b7aff;
  background: #f3f7ff;
}

.comment-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.comment-user {
  font-size: 28rpx;
  color: #222;
  font-weight: 500;
}

.comment-score {
  font-size: 24rpx;
  color: #ff7a00;
}

.comment-time {
  display: block;
  font-size: 22rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.comment-content {
  display: block;
  font-size: 26rpx;
  color: #444;
  line-height: 1.6;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>
