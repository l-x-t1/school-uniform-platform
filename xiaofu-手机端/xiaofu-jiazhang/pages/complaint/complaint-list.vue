<template>
  <view class="container">
    <scroll-view
      scroll-y
      class="list-scroll"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="refreshList"
      @scrolltolower="loadMore"
    >
      <view v-if="complaintList.length" class="list-wrap">
        <view
          v-for="item in complaintList"
          :key="item.id"
          class="complaint-card"
        >
          <view class="card-head">
            <text class="goods-name">{{ item.goodsName || '未填写商品' }}</text>
            <text class="status-tag" :class="item.status === '1' ? 'done' : 'pending'">
              {{ item.status === '1' ? '已处理' : '处理中' }}
            </text>
          </view>

          <view class="info-row">
            <text class="info-label">供应商</text>
            <text class="info-value">{{ item.supplierName || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">订单号</text>
            <text class="info-value">{{ item.orderNo || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">投诉类型</text>
            <text class="info-value">{{ item.complaintType || '-' }}</text>
          </view>

          <view class="card-foot">
            <text class="submit-time">{{ formatTime(item.createTime) }}</text>
            <button class="detail-btn" @click="goDetail(item)">投诉详情</button>
          </view>
        </view>

        <view class="load-tip" v-if="loading">加载中...</view>
        <view class="load-tip" v-else-if="!hasMore">没有更多投诉了</view>
      </view>

      <view v-else-if="!loading" class="empty-state">
        <text class="empty-title">暂无投诉记录</text>
        <text class="empty-sub">你提交的投诉会显示在这里</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      complaintList: [],
      pageNum: 1,
      pageSize: 20,
      loading: false,
      hasMore: true,
      refreshing: false
    }
  },
  onLoad() {
    this.fetchList()
  },
  onShow() {
    this.refreshList()
  },
  methods: {
    refreshList() {
      if (this.loading) {
        this.refreshing = false
        return
      }
      this.pageNum = 1
      this.hasMore = true
      this.complaintList = []
      this.refreshing = true
      this.fetchList()
    },
    loadMore() {
      if (!this.hasMore || this.loading) {
        return
      }
      this.pageNum += 1
      this.fetchList(true)
    },
    fetchList(isLoadMore = false) {
      if (this.loading) {
        return
      }
      this.loading = true
      uni.request({
        url: `${this.baseUrl}/school/parentComplaint/list2`,
        method: 'GET',
        header: {
          Authorization: uni.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        data: {
          userId: `${uni.getStorageSync('simId') || ''}`,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        },
        success: ({ data, statusCode }) => {
          if (statusCode === 200 && data.code === 200) {
            const rows = Array.isArray(data.rows) ? data.rows : []
            if (isLoadMore) {
              this.complaintList = this.complaintList.concat(rows)
            } else {
              this.complaintList = rows
            }
            this.hasMore = rows.length >= this.pageSize
            return
          }
          uni.showToast({ title: data.message || '加载投诉失败', icon: 'none' })
        },
        fail: () => {
          uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
        },
        complete: () => {
          this.loading = false
          this.refreshing = false
        }
      })
    },
    formatTime(value) {
      if (!value) {
        return ''
      }
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return value
      }
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      const hour = `${date.getHours()}`.padStart(2, '0')
      const minute = `${date.getMinutes()}`.padStart(2, '0')
      return `${month}-${day} ${hour}:${minute}`
    },
    goDetail(item) {
      uni.navigateTo({
        url: `/pages/complaint/complaint-detail?id=${item.id}`
      })
    }
  }
}
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background: #f6f8fb;
}

.list-scroll {
  height: 100vh;
}

.list-wrap {
  padding: 24rpx;
}

.complaint-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(15, 23, 42, 0.05);
}

.card-head,
.info-row,
.card-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-head {
  margin-bottom: 18rpx;
}

.goods-name {
  flex: 1;
  margin-right: 24rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
}

.status-tag {
  padding: 8rpx 18rpx;
  font-size: 22rpx;
  border-radius: 999rpx;
}

.status-tag.pending {
  color: #f97316;
  background: #fff7ed;
}

.status-tag.done {
  color: #16a34a;
  background: #f0fdf4;
}

.info-row {
  margin-bottom: 12rpx;
  gap: 24rpx;
}

.info-label {
  font-size: 24rpx;
  color: #6b7280;
}

.info-value {
  flex: 1;
  text-align: right;
  font-size: 24rpx;
  color: #111827;
  word-break: break-all;
}

.card-foot {
  margin-top: 24rpx;
}

.submit-time {
  font-size: 22rpx;
  color: #9ca3af;
}

.detail-btn {
  min-width: 150rpx;
  height: 60rpx;
  line-height: 60rpx;
  margin: 0;
  font-size: 24rpx;
  color: #fff;
  background: linear-gradient(135deg, #fb7185 0%, #f97316 100%);
  border-radius: 999rpx;
}

.detail-btn::after {
  border: none;
}

.load-tip,
.empty-state {
  text-align: center;
  color: #94a3b8;
}

.load-tip {
  padding: 16rpx 0 32rpx;
  font-size: 24rpx;
}

.empty-state {
  padding-top: 220rpx;
}

.empty-title {
  display: block;
  font-size: 30rpx;
  color: #334155;
}

.empty-sub {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
}
</style>
