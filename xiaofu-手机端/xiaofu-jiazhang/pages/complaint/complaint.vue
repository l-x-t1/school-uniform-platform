<template>
  <view class="container">
    <view class="summary-card" v-if="orderNo || supplierName || goodsName">
      <view class="summary-row" v-if="goodsName">
        <text class="summary-label">商品</text>
        <text class="summary-value">{{ goodsName }}</text>
      </view>
      <view class="summary-row" v-if="supplierName">
        <text class="summary-label">供应商</text>
        <text class="summary-value">{{ supplierName }}</text>
      </view>
      <view class="summary-row" v-if="orderNo">
        <text class="summary-label">订单号</text>
        <text class="summary-value">{{ orderNo }}</text>
      </view>
    </view>

    <view class="form-card">
      <view class="section">
        <text class="section-title">投诉选项</text>
        <view class="option-list">
          <view
            v-for="item in complaintOptions"
            :key="item"
            class="option-chip"
            :class="{ active: formData.complaintType === item }"
            @click="formData.complaintType = item"
          >
            {{ item }}
          </view>
        </view>
      </view>

      <view class="section">
        <text class="section-title">投诉原因</text>
        <textarea
          v-model="formData.reason"
          class="reason-box"
          maxlength="500"
          placeholder="请详细描述投诉原因，便于我们核实处理"
        />
        <view class="word-count">{{ formData.reason.length }}/500</view>
      </view>

      <view class="section">
        <text class="section-title">上传照片</text>
        <view class="photo-list">
          <view
            v-for="(item, index) in formData.photos"
            :key="item"
            class="photo-item"
          >
            <image class="photo-image" :src="item" mode="aspectFill" @click="previewImage(index)" />
            <view class="photo-remove" @click="removePhoto(index)">x</view>
          </view>
          <view class="photo-upload" @click="chooseImages">
            <text class="photo-upload-plus">+</text>
            <text class="photo-upload-text">上传照片</text>
          </view>
        </view>
      </view>
    </view>

    <button class="submit-btn" :disabled="submitting" @click="submitComplaint">
      {{ submitting ? '提交中...' : '提交投诉' }}
    </button>
  </view>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: getApp().globalData.baseUrl,
      submitting: false,
      orderId: '',
      orderNo: '',
      supplierName: '',
      schoolName: '',
      goodsName: '',
      complaintOptions: ['虚假宣传', '价格不实', '质量问题', '尺码不符', '发货延迟', '商品与描述不符'],
      formData: {
        complaintType: '虚假宣传',
        reason: '',
        photos: []
      }
    }
  },
  onLoad(options = {}) {
    this.orderId = options.orderId || ''
    this.orderNo = decodeURIComponent(options.orderNo || '')
    this.supplierName = decodeURIComponent(options.supplierName || '')
    this.schoolName = decodeURIComponent(options.schoolName || '')
    this.goodsName = decodeURIComponent(options.goodsName || '')
  },
  methods: {
    chooseImages() {
      const remainCount = 6 - this.formData.photos.length
      if (remainCount <= 0) {
        uni.showToast({ title: '最多上传6张照片', icon: 'none' })
        return
      }
      uni.chooseImage({
        count: remainCount,
        sizeType: ['compressed'],
        success: (res) => {
          this.formData.photos = [...this.formData.photos, ...(res.tempFilePaths || [])]
        }
      })
    },
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.formData.photos
      })
    },
    removePhoto(index) {
      this.formData.photos.splice(index, 1)
    },
    uploadPhotos() {
      const photos = this.formData.photos || []
      if (!photos.length) {
        return Promise.resolve([])
      }
      const tasks = photos.map((filePath) => new Promise((resolve) => {
        uni.uploadFile({
          url: `${this.baseUrl}/upload/image`,
          filePath,
          name: 'file',
          success: (res) => {
            try {
              const data = JSON.parse(res.data)
              if (data.code === 200) {
                resolve(data.data.url || data.url || '')
                return
              }
            } catch (error) {
            }
            resolve('')
          },
          fail: () => resolve('')
        })
      }))
      return Promise.all(tasks).then((list) => list.filter(Boolean))
    },
    async submitComplaint() {
      if (!this.formData.complaintType) {
        uni.showToast({ title: '请选择投诉选项', icon: 'none' })
        return
      }
      if (!this.formData.reason || this.formData.reason.trim().length < 5) {
        uni.showToast({ title: '请填写投诉原因', icon: 'none' })
        return
      }

      this.submitting = true
      const photoUrls = await this.uploadPhotos()
      const userInfo = uni.getStorageSync('userInfo') || {}
      const studentList = uni.getStorageSync('studentList') || []
      const firstStudent = studentList[0] || {}
      const payload = {
        userId: uni.getStorageSync('simId'),
        userName: userInfo.name || userInfo.nickName || '家长用户',
        deptId: firstStudent.deptId || firstStudent.schoolId || '',
        deptName: this.schoolName || firstStudent.schoolName || firstStudent.school || '',
        lxfs: userInfo.phone || userInfo.parm1 || uni.getStorageSync('phone') || '',
        feedbackUrl: photoUrls.join(','),
        feedbackContent: [
          `投诉类型：${this.formData.complaintType}`,
          this.goodsName ? `商品：${this.goodsName}` : '',
          this.supplierName ? `供应商：${this.supplierName}` : '',
          this.orderNo ? `订单号：${this.orderNo}` : '',
          `投诉原因：${this.formData.reason}`
        ].filter(Boolean).join('\n')
      }

      uni.request({
        url: `${this.baseUrl}/school/parentsFeedback`,
        method: 'POST',
        data: payload,
        header: {
          Authorization: uni.getStorageSync('token'),
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        success: ({ data }) => {
          if (data.code === 200) {
            uni.showToast({ title: '投诉提交成功', icon: 'success' })
            setTimeout(() => {
              uni.redirectTo({
                url: '/pages/complaint/complaint-list'
              })
            }, 1000)
            return
          }
          uni.showToast({ title: data.message || '投诉提交失败', icon: 'none' })
        },
        fail: () => {
          uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
        },
        complete: () => {
          this.submitting = false
        }
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

.summary-card,
.form-card {
  padding: 24rpx;
  margin-bottom: 24rpx;
  background: #fff;
  border-radius: 20rpx;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  margin-bottom: 16rpx;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-label {
  font-size: 26rpx;
  color: #666;
}

.summary-value {
  flex: 1;
  text-align: right;
  font-size: 26rpx;
  color: #333;
  word-break: break-all;
}

.section {
  margin-bottom: 28rpx;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18rpx;
}

.option-chip {
  padding: 14rpx 24rpx;
  font-size: 26rpx;
  color: #666;
  background: #f5f7fb;
  border: 1rpx solid #e5e7eb;
  border-radius: 999rpx;
}

.option-chip.active {
  color: #fff;
  background: #ff6b6b;
  border-color: #ff6b6b;
}

.reason-box {
  width: 100%;
  min-height: 240rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
  background: #f9fafb;
  border-radius: 16rpx;
  box-sizing: border-box;
}

.word-count {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #999;
  text-align: right;
}

.photo-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18rpx;
}

.photo-item,
.photo-upload {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 180rpx;
  height: 180rpx;
  overflow: hidden;
  background: #f9fafb;
  border-radius: 16rpx;
}

.photo-image {
  width: 100%;
  height: 100%;
}

.photo-remove {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 36rpx;
  height: 36rpx;
  line-height: 36rpx;
  color: #fff;
  text-align: center;
  background: rgba(0, 0, 0, 0.55);
  border-radius: 50%;
}

.photo-upload {
  border: 1rpx dashed #cbd5e1;
}

.photo-upload-plus {
  font-size: 52rpx;
  line-height: 1;
  color: #94a3b8;
}

.photo-upload-text {
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #94a3b8;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 30rpx;
  color: #fff;
  background: linear-gradient(135deg, #ff6b6b 0%, #f97316 100%);
  border: none;
  border-radius: 44rpx;
}

.submit-btn::after {
  border: none;
}

.submit-btn[disabled] {
  opacity: 0.7;
}
</style>
