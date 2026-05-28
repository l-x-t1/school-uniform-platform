<template>
  <view class="container">
    <view class="status-bar" :class="detail.replyState ? 'done' : 'pending'">
      {{ detail.replyState ? '投诉已处理' : '投诉处理中' }}
    </view>

    <view class="card">
      <text class="card-title">商品信息</text>
      <view class="info-row">
        <text class="info-label">商品</text>
        <text class="info-value">{{ detail.goodsName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">供应商</text>
        <text class="info-value">{{ detail.supplierName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">订单号</text>
        <text class="info-value">{{ detail.orderNo || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">提交时间</text>
        <text class="info-value">{{ detail.createTime || '-' }}</text>
      </view>
    </view>

    <view class="card">
      <text class="card-title">投诉内容</text>
      <view class="info-row">
        <text class="info-label">投诉类型</text>
        <text class="info-value">{{ detail.complaintType || '-' }}</text>
      </view>
      <view class="reason-box">
        <text class="reason-text">{{ detail.reason || '未填写投诉原因' }}</text>
      </view>
      <view v-if="detail.photos.length" class="photo-list">
        <image
          v-for="(photo, index) in detail.photos"
          :key="photo"
          class="photo-item"
          :src="photo"
          mode="aspectFill"
          @click="previewImage(index)"
        />
      </view>
    </view>

    <view class="card" v-if="detail.replyState">
      <text class="card-title">处理结果</text>
      <view class="reason-box">
        <text class="reason-text">{{ detail.replyContent || '暂无处理说明' }}</text>
      </view>
      <view class="reply-time" v-if="detail.replyTime">处理时间：{{ detail.replyTime }}</view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      complaintId: '',
      detail: {
        goodsName: '',
        supplierName: '',
        orderNo: '',
        complaintType: '',
        reason: '',
        createTime: '',
        replyState: 0,
        replyContent: '',
        replyTime: '',
        photos: []
      }
    }
  },
  onLoad(options = {}) {
    this.complaintId = options.id || ''
    this.fetchDetail()
  },
  methods: {
    fetchDetail() {
      uni.showLoading({ title: '加载中...' })
      uni.request({
        url: `${this.baseUrl}/school/parentsFeedback/${this.complaintId}`,
        method: 'GET',
        header: {
          Authorization: uni.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        data: {
          userId: uni.getStorageSync('simId')
        },
        success: ({ data }) => {
          if (data.code === 200 && data.data) {
            const item = data.data
            const parsed = this.parseComplaintContent(item.feedbackContent || '')
            this.detail = {
              goodsName: parsed.goodsName,
              supplierName: parsed.supplierName || item.deptName || '',
              orderNo: parsed.orderNo,
              complaintType: parsed.complaintType,
              reason: parsed.reason,
              createTime: this.formatDateTime(item.createTime || item.createDate),
              replyState: Number(item.replyState) || 0,
              replyContent: item.replyContent || '',
              replyTime: item.replyTime ? this.formatDateTime(item.replyTime || item.replyDate) : '',
              photos: item.feedbackUrl ? item.feedbackUrl.split(',').filter(Boolean) : []
            }
            return
          }
          uni.showToast({ title: data.message || '加载详情失败', icon: 'none' })
        },
        fail: () => {
          uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
        },
        complete: () => {
          uni.hideLoading()
        }
      })
    },
    parseComplaintContent(content) {
      const getValue = (label) => {
        const match = content.match(new RegExp(`${label}：([^\\n\\r]+)`))
        return match ? match[1].trim() : ''
      }
      return {
        complaintType: getValue('投诉类型'),
        goodsName: getValue('商品'),
        supplierName: getValue('供应商'),
        orderNo: getValue('订单号'),
        reason: getValue('投诉原因')
      }
    },
    formatDateTime(value) {
      if (!value) {
        return ''
      }
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return value
      }
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      const hour = `${date.getHours()}`.padStart(2, '0')
      const minute = `${date.getMinutes()}`.padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.detail.photos
      })
    }
  }
}
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  padding: 24rpx;
  background: #f6f8fb;
}

.status-bar {
  padding: 20rpx 24rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #fff;
  border-radius: 18rpx;
}

.status-bar.pending {
  background: linear-gradient(135deg, #fb923c 0%, #f97316 100%);
}

.status-bar.done {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.card {
  padding: 24rpx;
  margin-bottom: 20rpx;
  background: #fff;
  border-radius: 20rpx;
}

.card-title {
  display: block;
  margin-bottom: 20rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  margin-bottom: 16rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 25rpx;
  color: #6b7280;
}

.info-value {
  flex: 1;
  text-align: right;
  font-size: 25rpx;
  color: #111827;
  word-break: break-all;
}

.reason-box {
  padding: 20rpx;
  background: #f8fafc;
  border-radius: 16rpx;
}

.reason-text {
  font-size: 26rpx;
  line-height: 1.7;
  color: #334155;
}

.photo-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}

.photo-item {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
}

.reply-time {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #64748b;
}
</style>
