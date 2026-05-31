<template>
  <view class="container">
    <view class="status-bar" :class="detail.status === '1' ? 'done' : 'pending'">
      {{ detail.status === '1' ? '投诉已处理' : '投诉处理中' }}
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
        <text class="info-value">{{ formatDateTime(detail.createTime) || '-' }}</text>
      </view>
    </view>

    <view class="card">
      <text class="card-title">投诉内容</text>
      <view class="info-row">
        <text class="info-label">投诉类型</text>
        <text class="info-value">{{ detail.complaintType || '-' }}</text>
      </view>
      <view class="reason-box">
        <text class="reason-text">{{ detail.complaintReason || '未填写投诉原因' }}</text>
      </view>
      <view v-if="photoList.length" class="evidence-section">
        <text class="evidence-title">投诉凭证</text>
        <text class="evidence-tip">以下为提交投诉时上传的照片</text>
        <view class="photo-list">
          <image
            v-for="(photo, index) in photoList"
            :key="photo"
            class="photo-item"
            :src="photo"
            mode="aspectFill"
            @click="previewImage(index)"
          />
        </view>
      </view>
    </view>

    <view class="card" v-if="detail.status === '1' || detail.handleContent">
      <text class="card-title">处理结果</text>
      <view class="reason-box">
        <text class="reason-text">{{ detail.handleContent || '暂无处理说明' }}</text>
      </view>
      <view class="reply-time" v-if="detail.handleTime">
        处理时间：{{ formatDateTime(detail.handleTime) }}
      </view>
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
        complaintReason: '',
        createTime: '',
        status: '0',
        handleContent: '',
        handleTime: '',
        complaintImages: ''
      }
    }
  },
  computed: {
    photoList() {
      return (this.detail.complaintImages || '')
        .split(',')
        .filter(Boolean)
        .map((item) => this.resolveImage(item))
    }
  },
  onLoad(options = {}) {
    this.complaintId = options.id || ''
    this.fetchDetail()
  },
  methods: {
    resolveImage(url) {
      if (!url) {
        return ''
      }
      if (/^https?:\/\//i.test(url)) {
        return url
      }
      if (url.indexOf('/filestatics/') === 0) {
        const fileHost = (this.baseUrl || '').replace(/:\d+$/, ':9300').replace(/\/$/, '')
        return encodeURI(`${fileHost}${url}`)
      }
      const baseUrl = (this.baseUrl || '').replace(/\/$/, '')
      const fullUrl = `${baseUrl}${url.startsWith('/') ? '' : '/'}${url}`
      return encodeURI(fullUrl)
    },
    fetchDetail() {
      uni.showLoading({ title: '加载中...' })
      uni.request({
        url: `${this.baseUrl}/school/parentComplaint/${this.complaintId}`,
        method: 'GET',
        header: {
          Authorization: uni.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        success: ({ data }) => {
          if (data.code === 200 && data.data) {
            this.detail = data.data
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
        urls: this.photoList
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

.evidence-section {
  margin-top: 20rpx;
}

.evidence-title {
  display: block;
  margin-bottom: 8rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
}

.evidence-tip {
  display: block;
  font-size: 24rpx;
  color: #64748b;
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
